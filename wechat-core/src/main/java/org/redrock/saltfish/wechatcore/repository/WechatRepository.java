package org.redrock.saltfish.wechatcore.repository;

import com.google.gson.Gson;
import org.redrock.saltfish.common.bean.UserInfo;
import org.redrock.saltfish.common.component.StringUtil;
import org.redrock.saltfish.wechatcore.bean.Token;
import org.redrock.saltfish.wechatcore.component.RedisLock;
import org.redrock.saltfish.wechatcore.config.Api;
import org.redrock.saltfish.common.exception.WechatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Repository
public class WechatRepository {

    /**
     * 获取微信重要权限调用凭证access_token
     * @return
     */
    public String getAccessToken() {
        return jdbcTemplate.query(
                "select * from core where appId = ?",
                preparedStatement -> {
                    preparedStatement.setString(1, appId);
                },
                resultSet -> {
                    return (resultSet.next()) ? resultSet.getString("accessToken") : null;
                });
    }

    /**
     * 获取用户oauth鉴权调用凭证 access_token
     * @param code
     * @return
     */
    public Token getUserAccessToken(String code) throws WechatException {
        String api = String.format(Api.UserAccessTokenApi, appId, appSecret, code);
        Token token = restTemplate.getForObject(api, Token.class);
        if (token == null || !token.valid()) throw new WechatException(HttpStatus.BAD_REQUEST, "code 无效");
        return token;
    }

    /**
     * 考虑到可能后面会有多个服务实例，应该使用分布式锁
     * @param refreshToken
     * @return
     * @throws WechatException
     */
    public Token updateUserAccessToken(String refreshToken) throws WechatException {
        Token token;
        String refreshTokenKey = "refresh_token:" + refreshToken;
        if (!redisTemplate.hasKey(refreshTokenKey)) throw new WechatException(HttpStatus.BAD_REQUEST, "refresh_token 无效");
        String oldAccessToken = (String) redisTemplate.opsForHash().get(refreshTokenKey, "access_token");
        String accessTokenKey = "access_token:" + oldAccessToken;
        if (redisTemplate.hasKey(accessTokenKey)) {
            long expireIn = redisTemplate.getExpire(accessTokenKey, TimeUnit.SECONDS);
            if (expireIn > 5 * 60) {
                token = new Token();
                token.setAccessToken(oldAccessToken);
                token.setExpiresIn(expireIn);
                return token;
            }
        }
        String jwt = (String) redisTemplate.opsForHash().get(refreshTokenKey, "jwt");
        RedisLock redisLock = new RedisLock(redisTemplate, refreshToken);
        redisLock.lock();
            accessTokenKey = "access_token:" + redisTemplate.opsForHash().get(refreshTokenKey, "access_token");
            long expireIn;
            if (redisTemplate.hasKey(accessTokenKey) && (expireIn = redisTemplate.getExpire(accessTokenKey, TimeUnit.SECONDS)) > 5 * 60) {
                token = new Token();
                token.setAccessToken(oldAccessToken);
                token.setExpiresIn(expireIn);
                redisLock.unlock();
                return token;
            }
            String api = String.format(Api.RefreshUserAccessTokenApi, appId, refreshToken);
            token = restTemplate.getForObject(api, Token.class);
            if (token == null || !token.valid()) {
                redisLock.unlock();
                throw new WechatException(HttpStatus.BAD_REQUEST, "refresh_token 无效");
            }
            redisTemplate.opsForHash().put(refreshTokenKey, "access_token", token.getAccessToken());
            redisTemplate.delete(accessTokenKey);
            accessTokenKey = "access_token:" + token.getAccessToken();
            redisTemplate.opsForValue().set(accessTokenKey, jwt, 2, TimeUnit.HOURS);
        redisLock.unlock();
        return token;
    }

    /**
     * 获取用户身份信息
     * @param openid
     * @return
     */
    public UserInfo getUserInfo(String openid) throws WechatException {
        String accessToken = getAccessToken();
        String api = String.format(Api.UserInfoApi, accessToken, openid);
        UserInfo userInfo = restTemplate.getForObject(api, UserInfo.class);
        if (userInfo == null || !userInfo.valid()) throw new WechatException(HttpStatus.BAD_REQUEST, "openid 错误");
        return userInfo;
    }

    /**
     * 根据用户信息创建内部调用jwt
     * @param userInfo
     * @return
     */
    public String createJwt(UserInfo userInfo) {
        Gson gson = new Gson();
        Map<String, String> header = new HashMap<>();
        header.put("alg", "256");
        header.put("typ", "jwt");
        String headerStr = stringUtil.base64Encode(gson.toJson(header));
        String payload = stringUtil.base64Encode(gson.toJson(userInfo));
        String signature = stringUtil.getSHA256Str(headerStr + "." + payload + secret);
        return headerStr + "." + payload + "." + signature;
    }

    /**
     * 将相关信息存入session，同时删除无用的 refresh_token 与 access_token
     * @param jwt
     * @param token
     */
    public void saveJwtAndToken(String jwt, Token token) {
        // 通过openid 删除之前所有的权限数据
        String openidKey = "openid:" + token.getOpenid();
        if (redisTemplate.hasKey(openidKey)) {
            String oldRefreshToken = redisTemplate.opsForValue().get(openidKey);
            String refreshTokenKey = "refresh_token:" + oldRefreshToken;
            if (redisTemplate.hasKey(refreshTokenKey)) {
                String oldAccessToken = (String) redisTemplate.opsForHash().get(refreshTokenKey, "access_token");
                String accessTokenKey = "access_token:" + oldAccessToken;
                redisTemplate.delete(accessTokenKey);
            }
            redisTemplate.delete(refreshTokenKey);
        }
        //重新设置 openidKey的键值
        redisTemplate.opsForValue().set(openidKey, token.getRefreshToken(), 20, TimeUnit.DAYS);
        //重新设置 refreshTokenKey的键值
        String refreshTokenKey = "refresh_token:" + token.getRefreshToken();
        redisTemplate.opsForHash().put(refreshTokenKey, "access_token", token.getAccessToken());
        redisTemplate.opsForHash().put(refreshTokenKey, "jwt", jwt);
        redisTemplate.expire(refreshTokenKey, 15, TimeUnit.DAYS);
        //重新设置 accessTokenKey的键值
        String accessTokenKey = "access_token:" + token.getAccessToken();
        redisTemplate.opsForValue().set(accessTokenKey, jwt, 2, TimeUnit.HOURS);
    }

    /**
     * 检查jwt是否合法
     * @param jwt
     * @return
     */
    public boolean checkJwt(String jwt) {
        if (!stringUtil.isBlank(jwt)) {
            String[] items = jwt.split("\\.");
            if (items.length == 3) {
                String signature = stringUtil.getSHA256Str(items[0] + "." + items[1] + secret);
                if (signature.equalsIgnoreCase(items[3])) return true;
            }
        }
        return false;
    }

    @Autowired
    RestTemplate restTemplate;
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    StringUtil stringUtil;
    @Autowired
    RedisTemplate<String, String> redisTemplate;
    @Value("${wechat.appId}")
    String appId;
    @Value("${wechat.appSecret}")
    String appSecret;
    @Value("${core.secret}")
    String secret;
}

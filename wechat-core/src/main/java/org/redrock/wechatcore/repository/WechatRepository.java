package org.redrock.wechatcore.repository;

import com.google.gson.Gson;
import org.redrock.wechatcore.bean.Token;
import org.redrock.wechatcore.bean.UserInfo;
import org.redrock.wechatcore.component.RedisLock;
import org.redrock.wechatcore.component.StringUtil;
import org.redrock.wechatcore.config.Api;
import org.redrock.wechatcore.exception.WechatException;
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
        Token token = null;
        String jwtAndTokenKey = "refresh_token:" + refreshToken;
        if (!redisTemplate.hasKey(jwtAndTokenKey)) throw new WechatException(HttpStatus.BAD_REQUEST, "refresh_token 无效");
        String oldAccessToken = (String) redisTemplate.opsForHash().get(jwtAndTokenKey, "access_token");
        String jwtKey = "access_token:" + oldAccessToken;
        if (redisTemplate.hasKey(jwtKey)) {
            long expireIn = redisTemplate.getExpire(jwtKey, TimeUnit.SECONDS);
            if (expireIn > 5 * 60) {
                token = new Token();
                token.setExpiresIn(Integer.parseInt(String.valueOf(expireIn)));
                token.setAccessToken(oldAccessToken);
                token.setRefreshToken(refreshToken);
                token.setScope("sni_base");
                return token;
            }
            RedisLock redisLock = new RedisLock(redisTemplate, refreshToken);
            redisLock.lock();
                jwtKey = "access_token:" + redisTemplate.opsForHash().get(jwtAndTokenKey, "access_token");
                if (!redisTemplate.hasKey(jwtKey) || redisTemplate.getExpire(jwtKey, TimeUnit.SECONDS) < 5 * 60) {
                    String api = String.format(Api.RefreshUserAccessTokenApi, appId, refreshToken);
                    token = restTemplate.getForObject(api, Token.class);
                    if (token == null || !token.valid()) throw new WechatException(HttpStatus.BAD_REQUEST, "refresh_token 无效");
                    redisTemplate.delete(jwtKey);
                    String jwt = (String) redisTemplate.opsForHash().get(jwtAndTokenKey, "jwt");
                    redisTemplate.opsForHash().put(jwtAndTokenKey, "access_token", token.getAccessToken());
                    jwtKey = "access_token:" + token.getAccessToken();
                    redisTemplate.opsForValue().set(jwtKey, jwt, 2, TimeUnit.HOURS);
                }
            redisLock.unlock();
        }
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
        String headerStr = stringUtil.getBase64Str(gson.toJson(header));
        String payload = stringUtil.getBase64Str(gson.toJson(userInfo));
        String signature = stringUtil.getSHA256Str(headerStr + "." + payload + secret);
        return headerStr + "." + payload + "." + signature;
    }

    /**
     * 将相关信息存入session
     * @param jwt
     * @param token
     */
    public void saveJwtAndToken(String jwt, Token token) {
        redisTemplate.opsForValue().set("access_token:" + token.getAccessToken(), jwt,  2, TimeUnit.HOURS);
        String key = "refresh_token:" + token.getRefreshToken();
        redisTemplate.opsForHash().put(key, "access_token", token.getAccessToken());
        redisTemplate.opsForHash().put(key, "jwt", jwt);
        redisTemplate.expire(key, 15, TimeUnit.DAYS);
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

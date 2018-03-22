package org.redrock.saltfish.wechatcore.repository;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.redrock.saltfish.common.bean.UserInfo;
import org.redrock.saltfish.common.component.StringUtil;
import org.redrock.saltfish.wechatcore.bean.Token;
import org.redrock.saltfish.wechatcore.config.Api;
import org.redrock.saltfish.wechatcore.component.RedisLock;
import org.redrock.saltfish.common.exception.RequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
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
    public Token getUserAccessToken(String code) throws RequestException {
        String api = String.format(Api.UserAccessTokenApi, appId, appSecret, code);
        Token token = outRestTemplate.getForObject(api, Token.class);
        if (token == null || !token.valid()) throw new RequestException(HttpStatus.BAD_REQUEST, "code 无效");
        return token;
    }

    /**
     * 考虑到可能后面会有多个服务实例，应该使用分布式锁
     * @param refreshToken
     * @return
     * @throws RequestException
     */
    public Token updateUserAccessToken(String refreshToken) throws RequestException {
        Token token;
        String refreshTokenKey = "refresh_token:" + refreshToken;
        if (!redisTemplate.hasKey(refreshTokenKey)) throw new RequestException(HttpStatus.BAD_REQUEST, "refresh_token 无效");
        String oldAccessToken = redisTemplate.opsForValue().get(refreshTokenKey);
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
        RedisLock redisLock = new RedisLock(redisTemplate, refreshToken);
        redisLock.lock();
        accessTokenKey = "access_token:" + redisTemplate.opsForValue().get(refreshTokenKey);
        long expireIn;
        if (redisTemplate.hasKey(accessTokenKey) && (expireIn = redisTemplate.getExpire(accessTokenKey, TimeUnit.SECONDS)) > 5 * 60) {
            token = new Token();
            token.setAccessToken(oldAccessToken);
            token.setExpiresIn(expireIn);
            redisLock.unlock();
            return token;
        }
        String api = String.format(Api.RefreshUserAccessTokenApi, appId, refreshToken);
        token = outRestTemplate.getForObject(api, Token.class);
        if (token == null || !token.valid()) {
            redisLock.unlock();
            throw new RequestException(HttpStatus.BAD_REQUEST, "refresh_token 无效");
        }
        redisTemplate.opsForValue().set(refreshToken, token.getAccessToken());
        redisTemplate.delete(accessTokenKey);
        accessTokenKey = "access_token:" + token.getAccessToken();
        String openIdKey = "openId:" + token.getOpenid();
        String userInfo = (String) redisTemplate.opsForHash().get(openIdKey, "user_info");
        redisTemplate.opsForValue().set(accessTokenKey, userInfo, 2, TimeUnit.HOURS);
        redisLock.unlock();
        return token;
    }

    /**
     * 获取用户身份信息
     * @param openid
     * @return
     */
    public UserInfo getUserInfo(String openid) throws RequestException {
        String accessToken = getAccessToken();
        String api = String.format(Api.UserInfoApi, accessToken, openid);
        UserInfo userInfo = outRestTemplate.getForObject(api, UserInfo.class);
        if (userInfo == null || !userInfo.valid()) throw new RequestException(HttpStatus.BAD_REQUEST, "openid 错误");
        return userInfo;
    }

    /**
     * 根据用户信息创建内部调用jwt
     * @param payloadStr
     * @return
     */
    public String createJwt(String payloadStr) {
        JsonObject headerData = new JsonObject();
        headerData.addProperty("alg", "256");
        headerData.addProperty("typ", "jwt");
        String header = stringUtil.base64Encode(headerData.toString());
        String payload = stringUtil.base64Encode(payloadStr);
        String signature = stringUtil.getSHA256Str(header + "." + payload);
        return header + "." + payload + "." + signature;
    }

    public String createJwt(Object payload) {
        Gson gson = new Gson();
        String payloadStr = gson.toJson(payload);
        return createJwt(payloadStr);
    }

    /**
     * 将相关信息存入session，同时删除无用的 refresh_token 与 access_token
     * 获取用户详细信息
     * @param userInfo
     * @param token
     */
    public void saveTokenAndUserInfo(UserInfo userInfo, Token token) throws RequestException {
        String openId = token.getOpenid();
        Map<String, String> detailedUserInfoData = userRepository.getDetailedUserInfo(openId);
        String type = detailedUserInfoData.get("type");
        String detailedUserInfoJwt = createJwt(detailedUserInfoData.get("data"));
        //如果存在之前的用户权限则删除
        String openIdKey = "openId:" + openId;
        if (redisTemplate.hasKey(openIdKey)) {
            String oldRefreshToken = (String) redisTemplate.opsForHash().get(openIdKey, "refresh_token");
            String refreshTokenKey = "refresh_token:" + oldRefreshToken;
            if (redisTemplate.hasKey(refreshTokenKey)) {
                String oldAccessToken = redisTemplate.opsForValue().get(refreshTokenKey);
                String accessTokenKey = "access_token:" + oldAccessToken;
                redisTemplate.delete(accessTokenKey);
            }
            redisTemplate.delete(refreshTokenKey);
        }
        userInfo.setType(type);
        String userInfoJwt = createJwt(userInfo);
        //重新设置 openIdKey 的值
        redisTemplate.opsForHash().put(openIdKey,"user_info", userInfoJwt);
        redisTemplate.opsForHash().put(openIdKey, "detailed_user_info", detailedUserInfoJwt);
        redisTemplate.opsForHash().put(openIdKey, "refresh_token", token.getRefreshToken());
        redisTemplate.expire(openIdKey, 20, TimeUnit.DAYS);
        //重新舍则 refreshToken 的值
        String refreshTokenKey = "refresh_token:" + token.getRefreshToken();
        redisTemplate.opsForValue().set(refreshTokenKey, token.getAccessToken(), 15, TimeUnit.DAYS);
        //重新设置 accessToken 的值
        String accessTokenKey = "access_token:" + token.getAccessToken();
        redisTemplate.opsForValue().set(accessTokenKey, userInfoJwt, 2, TimeUnit.HOURS);
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

    @Resource(name = "outRestTemplate")
    RestTemplate outRestTemplate;
    @Autowired
    UserRepository userRepository;
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

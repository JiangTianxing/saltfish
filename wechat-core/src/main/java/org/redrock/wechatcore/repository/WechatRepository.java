package org.redrock.wechatcore.repository;

import com.google.gson.Gson;
import org.redrock.wechatcore.bean.Token;
import org.redrock.wechatcore.bean.UserInfo;
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
//        // 判断refresh_token 是否过期
//        String accessToken = redisTemplate.opsForValue().get("refresh_token:" + refreshToken);
//        if (stringUtil.isBlank(accessToken)) throw new WechatException(HttpStatus.BAD_REQUEST, "refresh_token 无效");
//        // 刷新token
//        String api = String.format(Api.RefreshUserAccessTokenApi, appId, refreshToken);
//        Token token = restTemplate.getForObject(api, Token.class);
//        if (token == null || !token.valid()) throw new WechatException(HttpStatus.BAD_REQUEST, "refresh_token 无效");
//        // 存储jwt，完成更新
//        String jwt = redisTemplate.opsForValue().get("access_token:" + token.getAccessToken());
//        if (stringUtil.isBlank(jwt)) {
//            UserInfo userInfo = getUserInfo(token.getOpenid());
//            jwt = createJwt(userInfo);
//        }
//        redisTemplate.opsForValue().set("access_token:" + token.getAccessToken(), jwt, 2, TimeUnit.HOURS);
//        redisTemplate.opsForValue()
//        return token;
        //判断 refresh_token 是否过期
        //刷新 access_token
        //更新 token
        redisTemplate.setEnableTransactionSupport(true);
        redisTemplate.setEnableTransactionSupport(false);
        return null;
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
        redisTemplate.opsForValue().set("refresh_token:" + token.getRefreshToken(), token.getAccessToken(), 20, TimeUnit.DAYS);
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
}

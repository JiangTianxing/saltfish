package org.redrock.wechatcore.repository;

import com.google.gson.Gson;
import org.redrock.wechatcore.bean.Token;
import org.redrock.wechatcore.bean.UserInfo;
import org.redrock.wechatcore.config.ApiConfiguration;
import org.redrock.wechatcore.exception.WechatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@Repository
public class WechatRepository {

    @Autowired
    RestTemplate restTemplate;
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    StringRepository stringRepository;
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
        String api = String.format(ApiConfiguration.UserAccessTokenApi, appId, appSecret, code);
        Token token = restTemplate.getForObject(api, Token.class);
        if (token == null || !token.valid()) throw new WechatException(HttpStatus.BAD_REQUEST, "code 无效");
        return token;

    }

    /**
     * 获取用户身份信息
     * @param openid
     * @return
     */
    public UserInfo getUserInfo(String openid) throws WechatException {
        String accessToken = getAccessToken();
        String api = String.format(ApiConfiguration.UserInfoApi, accessToken, openid);
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
        String headerStr = stringRepository.getBase64Str(gson.toJson(header));
        String payload = stringRepository.getBase64Str(gson.toJson(userInfo));
        String signature = stringRepository.getSHA256Str(headerStr + "." + payload + secret);
        return headerStr + "." + payload + "." + signature;
    }

    /**
     * 检查jwt是否合法
     * @param jwt
     * @return
     */
    public boolean checkJwt(String jwt) {
        if (!stringRepository.isBlank(jwt)) {
            String[] items = jwt.split("\\.");
            if (items.length == 3) {
                String signature = stringRepository.getSHA256Str(items[0] + "." + items[1] + secret);
                if (signature.equalsIgnoreCase(items[3])) return true;
            }
        }
        return false;
    }
}

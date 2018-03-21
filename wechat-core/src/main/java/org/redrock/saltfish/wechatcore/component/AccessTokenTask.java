package org.redrock.saltfish.wechatcore.component;

import org.redrock.saltfish.common.component.StringUtil;
import org.redrock.saltfish.common.component.TimeUtil;
import org.redrock.saltfish.wechatcore.repository.WechatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

import static org.redrock.saltfish.wechatcore.config.Api.AccessTokenApi;

@Component
public class AccessTokenTask {

    /**
     * 必须加分布式锁进行刷新
     * 一小时 和 每次重启服务时 自动刷新一次access_token
     */
    @Scheduled(fixedRate = 1000 * 60 * 60)
    public void refreshAccessToken() {
        String url = String.format(AccessTokenApi, appId, appSecret);
        Map<String, String> data = restTemplate.getForObject(url, HashMap.class);
        if (!stringUtil.isBlank(data.get("access_token"))) {
            String accessToken = data.get("access_token");
            if (stringUtil.isBlank(wechatRepository.getAccessToken())) {
                jdbcTemplate.update(
                        "insert into core(appId, appSecret, token, accessToken, time) values(?, ?, ?, ?, ?)",
                        preparedStatement -> {
                            preparedStatement.setString(1, appId);
                            preparedStatement.setString(2, appSecret);
                            preparedStatement.setString(3, token);
                            preparedStatement.setString(4, accessToken);
                            preparedStatement.setInt(5, timeUtil.getNowTime());
                        }
                );
            } else {
                jdbcTemplate.update(
                        "update core set accessToken = ? where appId = ?",
                        preparedStatement -> {
                            preparedStatement.setString(1, accessToken);
                            preparedStatement.setString(2, appId);
                        }
                );
            }
        }
    }

    @Value("${wechat.appId}")
    private String appId;
    @Value("${wechat.appSecret}")
    private String appSecret;
    @Value("${wechat.token}")
    private String token;

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private StringUtil stringUtil;
    @Autowired
    private TimeUtil timeUtil;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private WechatRepository wechatRepository;

}

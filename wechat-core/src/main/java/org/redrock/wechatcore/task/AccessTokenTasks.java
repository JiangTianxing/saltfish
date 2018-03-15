package org.redrock.wechatcore.task;

import org.redrock.wechatcore.repository.StringRepository;
import org.redrock.wechatcore.repository.TimeRepository;
import org.redrock.wechatcore.repository.WechatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.redrock.wechatcore.config.ApiConfiguration.*;

@Component
public class AccessTokenTasks {

    @Value("${wechat.appId}")
    private String appId;
    @Value("${wechat.appSecret}")
    private String appSecret;
    @Value("${wechat.token}")
    private String token;

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private StringRepository stringUtil;
    @Autowired
    private TimeRepository timeUtil;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private WechatRepository wechatRepository;

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
                        "update core set accessToke  = ? where appId = ?",
                        preparedStatement -> {
                            preparedStatement.setString(1, accessToken);
                            preparedStatement.setString(2, appId);
                        }
                );
            }
        }
    }
}

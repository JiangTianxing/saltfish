package org.redrock.wechatcore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RefreshScope
@RestController
public class IndexController {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Value("${word}")
    private String word;

    @GetMapping("/word")
    public String hello() {
        return word;
    }

    @GetMapping("/test")
    public String test() {
        String data = jdbcTemplate.query(
                "select * from token where id = 1",
                resultSet -> {
                    if (resultSet.next()) {
                        return resultSet.getString("token");
                    }
                    return null;
                });
        return data;
    }
}

package org.redrock.saltfish.wechatdemo.controller;

import org.redrock.saltfish.common.bean.UserInfo;
import org.redrock.saltfish.common.interceptor.annotation.Wechat;
import org.redrock.saltfish.common.interceptor.impl.JwtAuth;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

    @Wechat(JwtAuth.class)
    @GetMapping("/userInfo")
    public UserInfo index(UserInfo userInfo) {
        return userInfo;
    }
}

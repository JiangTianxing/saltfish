package org.redrock.saltfish.wechatdemo.controller;

import org.redrock.saltfish.common.bean.UserInfo;
import org.redrock.saltfish.common.interceptor.annotation.Wechat;
import org.redrock.saltfish.common.interceptor.impl.JwtAuth;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
public class IndexController {

    /**
     * 接受用户操作微信的相关信息
     * @param data
     * @return
     */
    @RequestMapping(value = "/index", method = {RequestMethod.GET, RequestMethod.POST})
    public String index(@RequestBody Map<String, Object> data) {
        System.out.println(data.toString());
        return data.toString();
    }

    /**
     * 利用 网关自动将access_token转化成的jwt 读取用户信息
     * @param userInfo
     * @return
     */
    @Wechat(JwtAuth.class)
    @GetMapping("/userinfo")
    public UserInfo userInfo(UserInfo userInfo) {
        return userInfo;
    }
}

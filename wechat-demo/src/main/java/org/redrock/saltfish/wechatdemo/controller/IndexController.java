package org.redrock.saltfish.wechatdemo.controller;

import org.redrock.saltfish.common.bean.DetailedUserInfo;
import org.redrock.saltfish.common.bean.UserInfo;
import org.redrock.saltfish.common.interceptor.annotation.Before;
import org.redrock.saltfish.common.interceptor.impl.DetailedUserInfoAuth;
import org.redrock.saltfish.common.interceptor.impl.UserInfoAuth;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
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
    @Before(UserInfoAuth.class)
    @GetMapping("/userinfo")
    public UserInfo userInfo(UserInfo userInfo) {
        return userInfo;
    }


    @Before(DetailedUserInfoAuth.class)
    @GetMapping("/detailed")
    public DetailedUserInfo detailed(DetailedUserInfo detailedUserInfo) {
        return detailedUserInfo;
    }
}
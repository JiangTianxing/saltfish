package org.redrock.saltfish.wechatcore.controller;

import org.redrock.saltfish.common.bean.UserInfo;
import org.redrock.saltfish.common.component.StringUtil;
import org.redrock.saltfish.common.interceptor.annotation.Before;
import org.redrock.saltfish.common.interceptor.impl.UserInfoAuth;
import org.redrock.saltfish.wechatcore.bean.Token;
import org.redrock.saltfish.common.exception.RequestException;
import org.redrock.saltfish.wechatcore.repository.WechatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
public class WechatController {

    /**
     * 获取微信权限accessToken
     * @return
     */
    @GetMapping("/access_token")
    public ResponseEntity<String> getAccessToken() {
        String accessToken = wechatRepository.getAccessToken();
        if (stringRepository.isBlank(accessToken)) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(accessToken, HttpStatus.OK);
    }

    /**
     * 通过 code 获取 oauth token 以及 内部调用凭证 jwt
     * @param code
     * @return
     * @throws RequestException
     */
    @GetMapping("/token/{code}")
    public ResponseEntity<Map> getToken(@PathVariable("code") Optional<String> code) throws RequestException {
        if (!code.isPresent()) throw new RequestException(HttpStatus.BAD_REQUEST, "code 参数不可为空");
        Token userToken = wechatRepository.getUserAccessToken(code.get());
        UserInfo userInfo = wechatRepository.getUserInfo(userToken.getOpenid());
        wechatRepository.saveTokenAndUserInfo(userInfo, userToken);
        Map<String, String> tokenWithJwt = new HashMap<>();
        tokenWithJwt.put("access_token", userToken.getAccessToken());
        tokenWithJwt.put("refresh_token", userToken.getRefreshToken());
        tokenWithJwt.put("expire_in", userToken.getExpiresIn() + "");
        return new ResponseEntity<>(tokenWithJwt, HttpStatus.OK);
    }

    @PatchMapping("/token")
    public ResponseEntity<Map> refreshTokenWithJwt(@RequestHeader("refresh_token") Optional<String> refreshToken) throws RequestException {
        if (!refreshToken.isPresent()) throw new RequestException(HttpStatus.BAD_REQUEST, "refresh_token 参数不可为空");
        Token userToken = wechatRepository.updateUserAccessToken(refreshToken.get());
        Map<String, String> tokenWithJwt = new HashMap<>();
        tokenWithJwt.put("access_token", userToken.getAccessToken());
        tokenWithJwt.put("expire_in", userToken.getExpiresIn() + "");
        return new ResponseEntity<>(tokenWithJwt, HttpStatus.OK);
    }

    /**
     * code 获取 测试接口
     * @param request
     * @param response
     * @throws IOException
     */
    @GetMapping("/test")
    public void test(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!stringRepository.isBlank(request.getParameter("code"))) {
            response.getWriter().println(request.getParameter("code"));
            return;
        }
        String apiUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect";
        String api = String.format(apiUrl, "wxdab44034ceb528e8", "http://jiangtianixng.s1.natapp.link/test");
        response.sendRedirect(api);
        return;
    }

    @Before(UserInfoAuth.class)
    @GetMapping("/jwt")
    public UserInfo jwt(UserInfo userInfo) {
        return userInfo;
    }

    @Autowired
    WechatRepository wechatRepository;
    @Autowired
    StringUtil stringRepository;
}
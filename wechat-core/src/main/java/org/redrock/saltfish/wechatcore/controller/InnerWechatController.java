package org.redrock.saltfish.wechatcore.controller;

import org.redrock.saltfish.common.bean.UserInfo;
import org.redrock.saltfish.common.component.StringUtil;
import org.redrock.saltfish.wechatcore.bean.Token;
import org.redrock.saltfish.common.exception.WechatException;
import org.redrock.saltfish.wechatcore.repository.WechatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/inner")
public class InnerWechatController {

    /**
     * 通过 code 获取 oauth验证token
     * @param code
     * @return
     * @throws WechatException
     */
    @GetMapping("/token/{code}")
    public ResponseEntity<Token> getToken(@PathVariable("code") Optional<String> code) throws WechatException {
        if (!code.isPresent()) {throw new WechatException(HttpStatus.BAD_REQUEST, "code 参数不可为空");}
        Token userToken = wechatRepository.getUserAccessToken(code.get());
        return new ResponseEntity<>(userToken, HttpStatus.OK);
    }

    /**
     * 通过 openid 获取用户信息
     * @param openid
     * @return
     * @throws WechatException
     */
    @GetMapping("/userInfo/{openid}")
    public ResponseEntity<UserInfo> getUserInfo(@PathVariable("openid") Optional<String> openid) throws WechatException {
        if (!openid.isPresent()) throw new WechatException(HttpStatus.BAD_REQUEST, "openid 参数不可为空");
        UserInfo userInfo = wechatRepository.getUserInfo(openid.get());
        return new ResponseEntity<>(userInfo, HttpStatus.OK);
    }


    @Autowired
    WechatRepository wechatRepository;
    @Autowired
    StringUtil stringRepository;
}

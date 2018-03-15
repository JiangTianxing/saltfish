package org.redrock.wechatcore.controller;

import org.redrock.wechatcore.repository.StringRepository;
import org.redrock.wechatcore.repository.WechatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "/core", headers = "version=temp")
@RestController
public class WechatController {

    @GetMapping("/access_token")
    public ResponseEntity<String> getAccessToken() {
        String accessToken = wechatRepository.getAccessToken();
        if (stringRepository.isBlank(accessToken)) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(accessToken, HttpStatus.OK);
    }

    @Autowired
    WechatRepository wechatRepository;
    @Autowired
    StringRepository stringRepository;
}

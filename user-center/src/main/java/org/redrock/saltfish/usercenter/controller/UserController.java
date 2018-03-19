package org.redrock.saltfish.usercenter.controller;

import org.redrock.saltfish.common.component.StringUtil;
import org.redrock.saltfish.common.exception.RequestException;
import org.redrock.saltfish.usercenter.service.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.util.Optional;

@RestController
public class UserController {

    @GetMapping("/userinfo/{openid}")
    public String userinfo(@PathVariable("openid") Optional<String> openid) throws RequestException {
        if (!openid.isPresent()) throw new RequestException(HttpStatus.BAD_REQUEST, "openid 不可为空");
        return userRepository.getDetailedUserInfo(openid.get());
    }

    @Autowired
    StringUtil stringUtil;
    @Autowired
    UserRepository userRepository;
}

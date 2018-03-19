package org.redrock.saltfish.wechatdemo.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class IndexController {

    @RequestMapping(value = "/index", method = {RequestMethod.GET, RequestMethod.POST})
    public String index(@RequestBody Map<String, Object> data) {
        System.out.println(data.toString());
        return data.toString();
    }
}

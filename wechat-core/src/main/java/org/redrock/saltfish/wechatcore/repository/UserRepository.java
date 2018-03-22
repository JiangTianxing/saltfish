package org.redrock.saltfish.wechatcore.repository;

import org.redrock.saltfish.common.exception.RequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Repository
public class UserRepository {

    public Map<String, String> getDetailedUserInfo(String openId) throws RequestException {
        String api = "http://USER-CENTER//userinfo/{openid}";
        ResponseEntity<Map> result = restTemplate.getForEntity(api, Map.class, openId);
        if (result.getStatusCode() == HttpStatus.OK) return result.getBody();
        throw new RequestException(HttpStatus.BAD_REQUEST, "openid 有误");
    }

    @Autowired
    private RestTemplate restTemplate;

}
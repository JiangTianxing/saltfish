package org.redrock.saltfish.common.service;

import org.redrock.saltfish.common.exception.RequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

public class UserRepository {

    public Map<String, String> getDetailedUserInfo(String openId) throws RequestException {
//        String api = "http://USER-CENTER//userinfo/{openid}";
        String api = "http://localhost:8084/userinfo/{openid}";
        ResponseEntity<Map> result = restTemplate.getForEntity(api, Map.class, openId);
        if (result.getStatusCode() == HttpStatus.OK) return result.getBody();
        throw new RequestException(HttpStatus.BAD_REQUEST, "openid 有误");
    }

    private RestTemplate restTemplate;

    public UserRepository(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}
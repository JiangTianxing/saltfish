package org.redrock.saltfish.gateway.repository;

import com.google.gson.Gson;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class UserRepository {

    public String getOpenIdFromUserInfoJwt(String jwt) {
        String[] items = jwt.split("\\.");
        String userInfo = items[1];
        Map data = new Gson().fromJson(userInfo, Map.class);
        return (String) data.get("openid");
    }
}

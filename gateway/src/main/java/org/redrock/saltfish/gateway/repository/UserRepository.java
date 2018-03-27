package org.redrock.saltfish.gateway.repository;

import com.google.gson.Gson;
import org.redrock.saltfish.gateway.component.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.logging.Logger;

@Repository
public class UserRepository {

    @Autowired
    StringUtil stringUtil;

    private Logger logger = Logger.getLogger(getClass().getName());

    public String getOpenIdFromUserInfoJwt(String jwt) {
//        String[] items = jwt.split("\\.");
//        String userInfo = stringUtil.base64Decode(items[1]);
//        String data =
//        try {
//            Map data = new Gson().fromJson(userInfo, Map.class);
//            return (String) data.get("openid");
//        } catch (Exception e) {
//            e.printStackTrace();
//            logger.info("<=====> jwt 有误 ");
//        }
        String openId = null;
        String[] item = jwt.split("\\.");
        String userInfoJson = stringUtil.base64Decode(item[1]);
        Map data = new Gson().fromJson(userInfoJson, Map.class);
        openId = (String) data.get("openid");
        logger.info("<=====> jwt 有误 ");
        return openId;
    }
}

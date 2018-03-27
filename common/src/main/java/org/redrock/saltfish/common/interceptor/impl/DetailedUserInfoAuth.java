package org.redrock.saltfish.common.interceptor.impl;

import com.google.gson.Gson;
import org.redrock.saltfish.common.bean.DetailedUserInfo;
import org.redrock.saltfish.common.bean.UserInfo;
import org.redrock.saltfish.common.component.StringUtil;
import org.redrock.saltfish.common.exception.RequestException;
import org.springframework.http.HttpStatus;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

public class DetailedUserInfoAuth extends UserInfoAuth {

    private Logger logger = Logger.getLogger(getClass().getName());

    private StringUtil stringUtil = new StringUtil();

    private final static String UserInfoPath = UserInfo.class.toString();
    private final static String DetailedUserInfoPath = DetailedUserInfo.class.toString();

    @Override
    public boolean interceptor(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler) throws Exception {
        String detailedUserInfoJwt = httpServletRequest.getHeader("detailed");
        if (stringUtil.isBlank(detailedUserInfoJwt)) {
            logger.info("<=====> detailedUserInfo is " + detailedUserInfoJwt);
            throw new RequestException(HttpStatus.NOT_FOUND, "查询不到用户信息");
        }
        return super.interceptor(httpServletRequest, httpServletResponse, handler);
//        String userInfoJwt = httpServletRequest.getHeader("userInfo");
//        if (stringUtil.isBlank(userInfoJwt)) {
//            logger.info("<=====> userInfo is " + userInfoJwt);
//            throw new RequestException(HttpStatus.NOT_FOUND, "查询不到用户信息");
//        }
//        String[] items = userInfoJwt.split("\\.");
//        if (items != null && items.length == 3) {
//            String userInfoJson = stringUtil.base64Decode(items[1]);
//            UserInfo userInfo = new Gson().fromJson(userInfoJson, UserInfo.class);
//            if (userInfo != null && userInfo.valid()) {
//                httpServletRequest.setAttribute(UserInfoPath, userInfo);
//                String detailedUserInfoJwt = httpServletRequest.getHeader("detailed");
//                if (stringUtil.isBlank(detailedUserInfoJwt)) {
//                    logger.info("<=====> detailedUserInfo is " + detailedUserInfoJwt);
//                    throw new RequestException(HttpStatus.NOT_FOUND, "查询不到用户信息");
//                }
//                DetailedUserInfo detailedUserInfo = new DetailedUserInfo();
//                detailedUserInfo.setDetailedUserInfo(httpServletRequest.getHeader("detailed").split("\\.")[1]);
//                detailedUserInfo.setType(userInfo.getType());
//                detailedUserInfo.setNickname(userInfo.getType());
//                detailedUserInfo.setSex(userInfo.getSex());
//                detailedUserInfo.setHeadimgurl(userInfo.getHeadimgurl());
//                detailedUserInfo.setOpenid(userInfo.getOpenid());
//                System.out.println(detailedUserInfo.toString());
//                httpServletRequest.setAttribute(DetailedUserInfoPath, detailedUserInfo);
//                return true;
//            }
//        }
//        return false;
    }
}

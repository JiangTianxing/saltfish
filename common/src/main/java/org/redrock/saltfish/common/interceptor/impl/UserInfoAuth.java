package org.redrock.saltfish.common.interceptor.impl;

import com.google.gson.Gson;
import org.redrock.saltfish.common.bean.UserInfo;
import org.redrock.saltfish.common.component.StringUtil;
import org.redrock.saltfish.common.exception.RequestException;
import org.redrock.saltfish.common.interceptor.BaseInterceptor;
import org.springframework.http.HttpStatus;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

public class UserInfoAuth implements BaseInterceptor {

    private final static String UserInfoPath = UserInfo.class.toString();

    private StringUtil stringUtil = new StringUtil();

    private Logger logger = Logger.getLogger(getClass().getName());

    @Override
    public boolean interceptor(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler) throws Exception {
        String userInfoJwt = httpServletRequest.getHeader("userInfo");
        if (stringUtil.isBlank(userInfoJwt)) {
            logger.info("<=====> userInfo is " + userInfoJwt);
            throw new RequestException(HttpStatus.NOT_FOUND, "查询不到用户信息");
        }
        String[] items = userInfoJwt.split("\\.");
        if (items != null && items.length == 3) {
            String userInfoJson = stringUtil.base64Decode(items[1]);
            UserInfo userInfo = new Gson().fromJson(userInfoJson, UserInfo.class);
            if (userInfo != null && userInfo.valid()) {
                httpServletRequest.setAttribute(UserInfoPath, userInfo);
                return true;
            }
        }
        return false;
    }
}

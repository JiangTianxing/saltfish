package org.redrock.saltfish.common.interceptor.impl;

import com.google.gson.Gson;
import org.redrock.saltfish.common.bean.UserInfo;
import org.redrock.saltfish.common.component.StringUtil;
import org.redrock.saltfish.common.exception.RequestException;
import org.redrock.saltfish.common.interceptor.BaseInterceptor;
import org.springframework.http.HttpStatus;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JwtAuth implements BaseInterceptor {

    private final static String UserInfoPath = UserInfo.class.toString();

    private StringUtil stringUtil = new StringUtil();

    @Override
    public boolean interceptor(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler) throws Exception {
        String authentication = httpServletRequest.getHeader("Authorization");
        if (!stringUtil.isBlank(authentication)) {
            authentication = authentication.trim();
            if (authentication.startsWith("jwt")) {
                String jwt = authentication.substring(authentication.indexOf(" ") + 1);
                String[] items = jwt.split("\\.");
                if (items != null && items.length == 3) {
                    String userInfoJson = stringUtil.base64Decode(items[1]);
                    UserInfo userInfo = new Gson().fromJson(userInfoJson, UserInfo.class);
                    if (userInfo != null && userInfo.valid()) {
                        httpServletRequest.setAttribute(UserInfoPath, userInfo);
                        return true;
                    }
                }
            }
        }
        throw new RequestException(HttpStatus.NOT_FOUND, "查询不到用户信息");
    }
}

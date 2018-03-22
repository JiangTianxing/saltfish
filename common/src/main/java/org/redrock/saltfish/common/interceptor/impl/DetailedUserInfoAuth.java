package org.redrock.saltfish.common.interceptor.impl;

import org.redrock.saltfish.common.component.StringUtil;
import org.redrock.saltfish.common.exception.RequestException;
import org.springframework.http.HttpStatus;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DetailedUserInfoAuth extends UserInfoAuth {

    private StringUtil stringUtil = new StringUtil();

    @Override
    public boolean interceptor(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler) throws Exception {
        String deatiledUserInfo = httpServletRequest.getHeader("detailed");
        if (!stringUtil.isBlank(deatiledUserInfo)) {
            super.interceptor(httpServletRequest, httpServletResponse, handler);
        }
        throw new RequestException(HttpStatus.NOT_FOUND, "查询不到用户信息");
    }
}

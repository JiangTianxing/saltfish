package org.redrock.wechatcore.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface BaseInterceptor {
    boolean interceptor(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler) throws Exception;
}
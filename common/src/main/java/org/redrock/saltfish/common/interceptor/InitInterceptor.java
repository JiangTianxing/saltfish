package org.redrock.saltfish.common.interceptor;

import org.redrock.saltfish.common.exception.RequestException;
import org.redrock.saltfish.common.interceptor.annotation.Wechat;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Component
public class InitInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler) throws Exception {
        if (handler != null) {
            if (handler.getClass().isAssignableFrom(HandlerMethod.class)) {
                Annotation[] methodAnnotations = ((HandlerMethod)handler).getMethod().getAnnotations();
                for (int i = 0; i < methodAnnotations.length; i++) {
                    Annotation annotation = methodAnnotations[i];
                    try {
                        Wechat wechat = (Wechat) annotation;
                        Class<? extends BaseInterceptor> interceptor = wechat.value();
                        Object object = Class.forName(interceptor.getCanonicalName()).newInstance();
                        Class[] clazzes = new Class[]{HttpServletRequest.class, HttpServletResponse.class, Object.class};
                        Method method = object.getClass().getMethod("interceptor", clazzes);
                        Object[] params = new Object[]{httpServletRequest, httpServletResponse, handler};
                        return (boolean) method.invoke(object, params);
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | ClassCastException e) {
                        Throwable cause = e.getCause();
                        if(cause instanceof RequestException){
                            httpServletResponse.setContentType("application/json;charset=UTF-8");
                            RequestException exception = (RequestException) cause;
                            int code = exception.getHttpStatus().value();
                            String msg = "{\"errmsg\":\""+exception.getMsg()+"\"}";
                            httpServletResponse.setStatus(code);
                            httpServletResponse.getWriter().write(msg);
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
    }
}
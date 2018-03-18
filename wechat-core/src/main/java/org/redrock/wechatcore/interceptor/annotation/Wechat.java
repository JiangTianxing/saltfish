package org.redrock.wechatcore.interceptor.annotation;

import org.redrock.wechatcore.interceptor.BaseInterceptor;

import java.lang.annotation.*;

@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.METHOD})
public @interface Wechat {
    Class<? extends BaseInterceptor> value();
}
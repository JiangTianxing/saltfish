package org.redrock.saltfish.common.interceptor.annotation;

import org.redrock.saltfish.common.interceptor.BaseInterceptor;

import java.lang.annotation.*;

@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.METHOD})
public @interface Wechat {
    Class<? extends BaseInterceptor> value();
}
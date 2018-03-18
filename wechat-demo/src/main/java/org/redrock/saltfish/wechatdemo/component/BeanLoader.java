package org.redrock.saltfish.wechatdemo.component;

import org.redrock.saltfish.common.interceptor.InitInterceptor;
import org.redrock.saltfish.common.resolver.UserInfoResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class BeanLoader {

    @Bean
    InitInterceptor initInterceptor() {
        return new InitInterceptor();
    }

    @Bean
    UserInfoResolver userInfoResolver() {
        return new UserInfoResolver();
    }
}

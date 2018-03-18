package org.redrock.saltfish.wechatdemo.config;

import org.redrock.saltfish.common.component.JsonToHttpMessageConverter;
import org.redrock.saltfish.common.component.MyRestTemplate;
import org.redrock.saltfish.common.interceptor.InitInterceptor;
import org.redrock.saltfish.common.resolver.UserInfoResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

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

    @Bean
    JsonToHttpMessageConverter jsonToHttpMessageConverter() {
        return new JsonToHttpMessageConverter();
    }

    @Bean
    RestTemplate restTemplate() {
        return new MyRestTemplate();
    }
}

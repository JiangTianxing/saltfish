package org.redrock.saltfish.wechatdemo.config;

import org.redrock.saltfish.common.bean.DetailedUserInfo;
import org.redrock.saltfish.common.component.JsonToHttpMessageConverter;
import org.redrock.saltfish.common.interceptor.InitInterceptor;
import org.redrock.saltfish.common.resolver.DetailedUserInfoResolver;
import org.redrock.saltfish.common.resolver.UserInfoResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.util.List;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer{

    @Autowired
    InitInterceptor initInterceptor;

    @Autowired
    UserInfoResolver userInfoResolver;
    @Autowired
    DetailedUserInfoResolver detailedUserInfoResolver;

    @Autowired
    JsonToHttpMessageConverter jsonToHttpMessageConverter;

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(jsonToHttpMessageConverter);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(initInterceptor).addPathPatterns("/**");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(userInfoResolver);
        resolvers.add(detailedUserInfoResolver);
    }
}
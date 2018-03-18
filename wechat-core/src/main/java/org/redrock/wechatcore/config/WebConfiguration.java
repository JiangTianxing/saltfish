package org.redrock.wechatcore.config;

import org.redrock.wechatcore.component.UserInfoResolver;
import org.redrock.wechatcore.interceptor.InitInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

@Configuration
public class WebConfiguration extends WebMvcConfigurerAdapter{

    @Autowired
    InitInterceptor initInterceptor;
    @Autowired
    UserInfoResolver userInfoResolver;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(initInterceptor).addPathPatterns("/**");
        super.addInterceptors(registry);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(userInfoResolver);
        super.addArgumentResolvers(argumentResolvers);
    }
}

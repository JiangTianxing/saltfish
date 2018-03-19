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

    /**
     * 加载 Wechat 注解拦截器，实现注解功能调用
     * @return
     */
    @Bean
    InitInterceptor initInterceptor() {
        return new InitInterceptor();
    }

    /**
     * 加载 UserInfo 类型参数自动注入，必须结合 Wechat(JwtAuth.class) 使用
     * @return
     */
    @Bean
    UserInfoResolver userInfoResolver() {
        return new UserInfoResolver();
    }

    /**
     * 消息转换器
     * @return
     */
    @Bean
    JsonToHttpMessageConverter jsonToHttpMessageConverter() {
        return new JsonToHttpMessageConverter();
    }

    /**
     * 
     * @return
     */
    @Bean
    RestTemplate restTemplate() {
        return new MyRestTemplate();
    }
}

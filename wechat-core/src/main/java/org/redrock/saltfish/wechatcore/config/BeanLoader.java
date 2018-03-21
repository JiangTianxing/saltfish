package org.redrock.saltfish.wechatcore.config;

import org.apache.commons.dbcp.BasicDataSource;
import org.redrock.saltfish.common.service.UserRepository;
import org.redrock.saltfish.wechatcore.component.JsonToHttpMessageConverter;
import org.redrock.saltfish.common.component.StringUtil;
import org.redrock.saltfish.common.component.TimeUtil;
import org.redrock.saltfish.common.interceptor.InitInterceptor;
import org.redrock.saltfish.common.resolver.UserInfoResolver;
import org.redrock.saltfish.common.resolver.RequestExceptionResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BeanLoader {
    /**
     * 添加 Text/Plain 格式的消息转换器
     */
    @Bean
    @LoadBalanced
    RestTemplate restTemplate(@Autowired JsonToHttpMessageConverter messageConverter) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(messageConverter);
        return restTemplate;
    }

    /**
     * 注册user服务
     * @param restTemplate
     * @return
     */
    @Bean
    UserRepository userRepository(@Autowired RestTemplate restTemplate) {
        return new UserRepository(restTemplate);
    }

    /**
     * 注册全局的wechat异常处理器
     */
    @Bean
    RequestExceptionResolver requestExceptionResolver() {
        return new RequestExceptionResolver();
    }

    /**
     * 配置数据源
     */
    @Bean
    public BasicDataSource dataSource(
            @Value("${spring.datasource.url}") String url,
            @Value("${spring.datasource.username}") String username,
            @Value("${spring.datasource.password}") String password,
            @Value("${spring.datasource.initialSize}") int initialSize,
            @Value("${spring.datasource.maxActive}") int maxActive,
            @Value("${spring.datasource.minIdle}") int maxIdle,
            @Value("${spring.datasource.maxWait}") int maxWait) {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setInitialSize(initialSize);
        dataSource.setMaxActive(maxActive);
        dataSource.setMaxIdle(maxIdle);
        dataSource.setMaxWait(maxWait);
        return dataSource;
    }

    /**
     * 配置 UserInfo 参数自动注入解析器
     * @return
     */
    @Bean
    UserInfoResolver userInfoResolver() {
        return new UserInfoResolver();
    }

    @Bean
    StringUtil stringUtil() {
        return new StringUtil();
    }

    @Bean
    TimeUtil timeUtil() {
        return new TimeUtil();
    }

    @Bean
    InitInterceptor initInterceptor() {
        return new InitInterceptor();
    }

    @Bean
    JsonToHttpMessageConverter jsonToHttpMessageConverter() {
        return new JsonToHttpMessageConverter();
    }

}

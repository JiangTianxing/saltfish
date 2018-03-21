package org.redrock.saltfish.usercenter.config;

import org.apache.commons.dbcp.BasicDataSource;
import org.redrock.saltfish.common.component.StringUtil;
import org.redrock.saltfish.common.component.TimeUtil;
import org.redrock.saltfish.common.resolver.RequestExceptionResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class BeanLoader {

    @Bean
    StringUtil stringUtil() {
        return new StringUtil();
    }

    @Bean
    TimeUtil timeUtil() {
        return new TimeUtil();
    }

    /**
     * 开启内存泄漏监听器
     */
//    @Bean
//    public ServletListenerRegistrationBean servletListenerRegistrationBean(){
//        ServletListenerRegistrationBean servletListenerRegistrationBean = new ServletListenerRegistrationBean();
//        servletListenerRegistrationBean.setListener(new ClassLoaderLeakPreventor());
//        return servletListenerRegistrationBean;
//    }

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
}
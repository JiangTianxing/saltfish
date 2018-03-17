package org.redrock.wechatcore.config;

import org.apache.commons.dbcp.BasicDataSource;
import org.redrock.wechatcore.component.WechatMappingJackson2HttpMessageConverter;
import org.redrock.wechatcore.component.WechatExceptionResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import se.jiderhamn.classloader.leak.prevention.ClassLoaderLeakPreventor;
import javax.sql.DataSource;

@Configuration
public class BeanLoader {
    /**
     * 添加 Text/Plain 格式的消息转换器
     */
    @Bean
    RestTemplate restTemplate(@Autowired WechatMappingJackson2HttpMessageConverter messageConverter) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(messageConverter);
        return restTemplate;
    }

    /**
     * 注册全局的wechat异常处理器
     */
    @Bean
    WechatExceptionResolver wechatExceptionResolver() {
        return new WechatExceptionResolver();
    }

    /**
     * 开启内存泄漏监听器
     */
    @Bean
    public ServletListenerRegistrationBean servletListenerRegistrationBean(){
        ServletListenerRegistrationBean servletListenerRegistrationBean = new ServletListenerRegistrationBean();
        servletListenerRegistrationBean.setListener(new ClassLoaderLeakPreventor());
        return servletListenerRegistrationBean;
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
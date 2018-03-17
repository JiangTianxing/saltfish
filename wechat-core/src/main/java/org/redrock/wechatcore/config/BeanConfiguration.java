package org.redrock.wechatcore.config;

import org.redrock.wechatcore.exception.WechatExceptionResolver;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import se.jiderhamn.classloader.leak.prevention.ClassLoaderLeakPreventor;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class BeanConfiguration {
    /**
     * 添加 Text/Plain 格式的消息转换器
     * @return
     */
    @Bean
    RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new WxMappingJackson2HttpMessageConverter());
        return restTemplate;
    }


    /**
     * 注册全局的wechat异常处理器
     * @return
     */
    @Bean
    WechatExceptionResolver wechatExceptionResolver() {
        return new WechatExceptionResolver();
    }

    /**
     * 开启内存泄漏监听器
     * @return
     */
    @Bean
    public ServletListenerRegistrationBean servletListenerRegistrationBean(){
        ServletListenerRegistrationBean servletListenerRegistrationBean = new ServletListenerRegistrationBean();
        servletListenerRegistrationBean.setListener(new ClassLoaderLeakPreventor());
        return servletListenerRegistrationBean;
    }

    static class WxMappingJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {
        WxMappingJackson2HttpMessageConverter(){
            List<MediaType> mediaTypes = new ArrayList<>();
            mediaTypes.add(MediaType.TEXT_PLAIN);
            setSupportedMediaTypes(mediaTypes);
        }
    }
}

package org.redrock.wechatcore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import se.jiderhamn.classloader.leak.prevention.ClassLoaderLeakPreventor;

@ComponentScan(basePackages = {"org.redrock.wechatcore.*"})
@EnableEurekaClient
@SpringBootApplication
public class WechatCoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(WechatCoreApplication.class, args);
	}

	@Bean
	public ServletListenerRegistrationBean servletListenerRegistrationBean(){
		ServletListenerRegistrationBean servletListenerRegistrationBean = new ServletListenerRegistrationBean();
		servletListenerRegistrationBean.setListener(new ClassLoaderLeakPreventor());
		return servletListenerRegistrationBean;
	}
}

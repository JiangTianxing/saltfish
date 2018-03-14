package org.redrock.wechatcore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {"org.redrock.wechatcore.*"})
@EnableEurekaClient
@SpringBootApplication
public class WechatCoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(WechatCoreApplication.class, args);
	}
}

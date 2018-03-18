package org.redrock.saltfish.wechatdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {"org.redrock.saltfish.wechatdemo.*"})
@EnableEurekaClient
@SpringBootApplication
public class WechatDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(WechatDemoApplication.class, args);
	}
}

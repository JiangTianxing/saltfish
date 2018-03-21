package org.redrock.saltfish.wechatcore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@EnableScheduling
@ComponentScan(basePackages = {"org.redrock.saltfish.wechatcore.*"})
@RestController
@EnableEurekaClient
@SpringBootApplication
public class WechatCoreApplication {

	@GetMapping("/")
	public String hello() {
		return "hello";
	}

	public static void main(String[] args) {
		SpringApplication.run(WechatCoreApplication.class, args);
	}


}
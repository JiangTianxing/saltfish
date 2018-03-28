package org.redrock.saltfish.hello;

import brave.Tracer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.logging.Logger;

@RestController
@SpringBootApplication
public class HelloApplication {

	public static void main(String[] args) {
		SpringApplication.run(HelloApplication.class, args);
	}

	private Logger logger = Logger.getLogger(getClass().getName());

	@Autowired
	Tracer tracer;

	@LoadBalanced
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Autowired
	RestTemplate restTemplate;

	@GetMapping("/hello")
	public String world() {
		logger.info("hello");
		return "hello";
	}

	@GetMapping("/hi")
	public String hi() {
		logger.info("hi");

		String hello = restTemplate.getForObject("http://WORLD/world", String.class);
		return hello + " world, this is from hello";
	}
}
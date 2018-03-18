package org.redrock.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class BeanLoader {

    @Value("${test}")
    List<String> test;

    @Bean
    List<String> list() {
        return test;
    }
}

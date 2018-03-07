package org.redrock.feigh;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(serviceId = "hello-service")
public interface HelloService {
    @GetMapping("/hello")
    String sayHi();
}

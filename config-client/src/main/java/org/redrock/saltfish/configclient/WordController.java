package org.redrock.saltfish.configclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RefreshScope
@RestController
public class WordController {

    @Value("${word:hhhh}")
    String word;

    @GetMapping("/word")
    public String word() {
        return word;
    }
}

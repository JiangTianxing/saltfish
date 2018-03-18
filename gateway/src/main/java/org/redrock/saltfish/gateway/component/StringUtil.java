package org.redrock.saltfish.gateway.component;

import org.springframework.stereotype.Component;

@Component
public class StringUtil {
    public boolean isBlank(String str) {
        return str == null || str.trim().equalsIgnoreCase("");
    }
}
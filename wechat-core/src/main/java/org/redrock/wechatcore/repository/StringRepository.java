package org.redrock.wechatcore.repository;

import org.springframework.stereotype.Component;

@Component
public class StringRepository {
    public boolean isBlank(String str) {
        return str == null || str.trim().equalsIgnoreCase("");
    }
}
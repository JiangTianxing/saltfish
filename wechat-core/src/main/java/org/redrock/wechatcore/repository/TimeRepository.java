package org.redrock.wechatcore.repository;

import org.springframework.stereotype.Component;

@Component
public class TimeRepository {
    public int getNowTime() {
        return (int) (System.currentTimeMillis()/1000);
    }
}

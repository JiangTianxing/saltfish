package org.redrock.saltfish.common.component;

import org.springframework.stereotype.Component;

@Component
public class TimeUtil {
    public int getNowTime() {
        return (int) (System.currentTimeMillis()/1000);
    }
}

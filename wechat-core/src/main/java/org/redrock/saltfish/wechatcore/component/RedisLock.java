package org.redrock.saltfish.wechatcore.component;

import org.springframework.data.redis.core.RedisTemplate;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class RedisLock {

    private static final String LOCKED = "LOCKED";

    private static final long TIME_OUT = 30000;

    public static final int EXPIRE = 60;

    private String key;

    private int expireTime = EXPIRE;

    private long timeOut = TIME_OUT;

    private volatile boolean isLocked = false;

    private RedisTemplate<String, String> redisTemplate;

    public RedisLock(RedisTemplate<String, String> redisTemplate, String key) {
        this.key = key;
        this.redisTemplate = redisTemplate;
    }

    public void lock() {
        long nowTime = System.nanoTime();
        long timeout = timeOut * 1000000;
        final Random r = new Random();
        while ((System.nanoTime() - nowTime) < timeout) {
            if (redisTemplate.opsForValue().setIfAbsent(key, LOCKED)) {
                isLocked = true;
                redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
                break;
            }
            try {
                Thread.sleep(3, r.nextInt(50000));
            } catch (InterruptedException e) {
            }
        }

    }

    public void unlock() {
        if (isLocked) redisTemplate.delete(key);
    }
}

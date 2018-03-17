package org.redrock.wechatcore;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WechatCoreApplicationTests {

	@Autowired
	RedisTemplate<String, String> redisTemplate;

	@Test
	public void contextLoads() {
		redisTemplate.opsForValue().set("hello", "world");
		redisTemplate.opsForValue().get("hello");
	}

}

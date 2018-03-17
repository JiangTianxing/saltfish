package org.redrock.wechatcore;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
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
		String key = "hello";
		String value = "world";
		redisTemplate.opsForValue().set(key, value);
		System.out.println();
		Assert.assertTrue(redisTemplate.opsForValue().get(key).equalsIgnoreCase(value));
	}

}

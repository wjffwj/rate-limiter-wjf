package com.wjf.controller.ratelimiter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/rate")
public class RedisTestController {
    @Autowired
    private RedisManager redisManager;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @RequestMapping(value = "/limit")
    public String ratelimitTest() {
        String key = "test_ratelimit_key";
        int max = 1;//令牌桶大小
        int rate = 1;//令牌每秒恢复速度
        Boolean allow = redisManager.rateLimit(key, max, rate);
        if (allow) {
            return "ok";
        } else {
            return "no ok";
        }

    }

    @RequestMapping(value = "/test")
    public String testRedisOk() {
        stringRedisTemplate.opsForValue().set("testtest", "testtest");
        String result = stringRedisTemplate.opsForValue().get("testtest");
        return result;
    }
}

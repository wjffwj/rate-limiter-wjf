package com.wjf.controller.ratelimiter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RedisManager {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public boolean rateLimit(String key, int max, int rate) {
        List<String> list = new ArrayList<>(1);
        list.add(key);
        // 执行 lua 脚本
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        // 指定 lua 脚本
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("script.lua")));
        redisScript.setResultType(Long.class);
        Long execute = stringRedisTemplate.execute(redisScript, list, Integer.toString(max), Integer.toString(rate), Long.toString(System.currentTimeMillis()));
        if(execute==1L) {
            return true;
        }else{
            return false;
        }
    }

}

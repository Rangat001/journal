package com.rgt.journal.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
public class RedisTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Disabled
    @Test
    void redistest(){
        redisTemplate.opsForValue().set("email","lgshop617@gmail.com");
        Object eamil = redisTemplate.opsForValue().get("email");
    }

}

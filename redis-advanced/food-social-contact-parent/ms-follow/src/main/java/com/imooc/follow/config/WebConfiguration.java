package com.imooc.follow.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@Configuration
public class WebConfiguration {

    @Resource
    private RedisTemplate redisTemplate;
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}

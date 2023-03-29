package com.imooc.seckill.config;

import com.imooc.seckill.tools.RedisLock;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@Configuration
public class WebConfiguration {

    @Resource
    private RedisTemplate redisTemplate;
    @LoadBalanced
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public RedisLock redisLock() {
        return new RedisLock(redisTemplate);
    }
}

package com.imooc.feeds;

import com.imooc.follow.client.FollowClient;
import com.imooc.oauth2.client.Oauth2ServerClient;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author E.T
 * @date 2023/4/19
 */
@MapperScan("com.imooc.feeds.mapper")
@ComponentScan(basePackages = {"com.imooc.commons", "com.imooc.feeds"})
@SpringBootApplication
@EnableFeignClients(clients = {FollowClient.class, Oauth2ServerClient.class})
public class FeedsApplication {
    public static void main(String[] args) {
        SpringApplication.run(FeedsApplication.class, args);
    }
}

package com.imooc.follow;

import com.imooc.diners.client.DinersClient;
import com.imooc.oauth2.client.Oauth2ServerClient;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author E.T
 * @date 2023/4/16
 */
@MapperScan(basePackages = "com.imooc.follow.mapper")
@EnableFeignClients(clients = {Oauth2ServerClient.class, DinersClient.class})
@SpringBootApplication
public class FollowApplication {
    public static void main(String[] args) {
        SpringApplication.run(FollowApplication.class, args);
    }
}

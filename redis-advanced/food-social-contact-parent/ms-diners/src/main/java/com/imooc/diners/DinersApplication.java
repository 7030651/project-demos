package com.imooc.diners;


import com.imooc.oauth2.client.Oauth2ServerClient;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@MapperScan(basePackages = "com.imooc.diners.mapper")
@ComponentScan(basePackages = {"com.imooc.commons.*", "com.imooc.diners.*"})
@SpringBootApplication
@EnableFeignClients(clients = {Oauth2ServerClient.class})
public class DinersApplication {
    public static void main(String[] args) {
        SpringApplication.run(DinersApplication.class, args);
    }
}

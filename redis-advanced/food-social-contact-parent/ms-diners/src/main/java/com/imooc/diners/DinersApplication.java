package com.imooc.diners;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;

@MapperScan(basePackages = "com.imooc.diners.mapper")
@ComponentScan(basePackages = {"com.imooc.commons.*", "com.imooc.diners.*"})
@SpringBootApplication
public class DinersApplication {
    public static void main(String[] args) {
        SpringApplication.run(DinersApplication.class, args);
    }
}

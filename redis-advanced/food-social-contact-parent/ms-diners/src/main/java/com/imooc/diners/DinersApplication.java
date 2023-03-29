package com.imooc.diners;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@MapperScan(basePackages = "com.imooc.diners.mapper")
@ComponentScan(basePackages = {"com.imooc.commons.*", "com.imooc.diners.*"})
@SpringBootApplication
public class DinersApplication {
    public static void main(String[] args) {
        SpringApplication.run(DinersApplication.class, args);
    }
}

package com.imooc.seckill;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author E.T
 * @date 2023/1/5
 */
@MapperScan(basePackages = "com.imooc.seckill.mapper")
@ComponentScan(basePackages = {"com.imooc.commons.*", "com.imooc.seckill.*"})
@SpringBootApplication
public class SeckillApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeckillApplication.class, args);
    }
}

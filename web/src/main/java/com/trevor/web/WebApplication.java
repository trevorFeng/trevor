package com.trevor.web;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

import java.util.List;

/**
 * @author trevor
 * @date 2019/3/1 11:40
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.trevor"})
@MapperScan("com.trevor.dao")
@EnableCaching
public class WebApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }
}

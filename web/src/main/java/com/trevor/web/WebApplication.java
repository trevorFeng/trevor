package com.trevor.web;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;

/**
 * @author trevor
 * @date 2019/3/1 11:40
 */
@SpringBootApplication
@ServletComponentScan
@ComponentScan(basePackages = {"com.trevor"})
@MapperScan("com.trevor.dao")
@EnableCaching
@EnableScheduling
@Configuration
public class WebApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }
}

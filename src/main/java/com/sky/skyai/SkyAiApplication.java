package com.sky.skyai;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.sky.skyai.mapper")
@SpringBootApplication
public class SkyAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SkyAiApplication.class, args);
    }

}

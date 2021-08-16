package com.monetware.ringinterview;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import tk.mybatis.spring.annotation.MapperScan;

@EnableScheduling
@EnableSwagger2
@SpringBootApplication
@MapperScan(basePackages = "com.monetware.ringinterview.business.dao.*")
public class RingInterviewApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(RingInterviewApiApplication.class, args);
    }

}

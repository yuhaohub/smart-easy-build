package com.yuhao.smarteasybuild;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.yuhao.smarteasybuild.mapper")
public class SmartEasyBuildApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartEasyBuildApplication.class, args);
    }

}

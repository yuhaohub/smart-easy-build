package com.yuhao.smarteasybuild;

import dev.langchain4j.community.store.embedding.redis.spring.RedisEmbeddingStoreAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {RedisEmbeddingStoreAutoConfiguration.class})
@MapperScan("com.yuhao.smarteasybuild.mapper")
public class SmartEasyBuildApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartEasyBuildApplication.class, args);
    }

}

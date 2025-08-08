package com.yuhao.smarteasybuild.ai;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CodeGeneratorServiceFactory {
    @Resource
    private ChatModel chatModel;

    @Bean
    public CodeGeneratorService codeGeneratorService() {
        return AiServices.create(CodeGeneratorService.class, chatModel);
    }
}

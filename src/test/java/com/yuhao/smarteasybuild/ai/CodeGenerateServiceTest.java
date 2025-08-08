package com.yuhao.smarteasybuild.ai;

import cn.hutool.core.lang.Assert;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CodeGenerateServiceTest {
    @Resource
    CodeGeneratorService codeGeneratorService;

    @Test
    void generateHtmlCode() {
        String result = codeGeneratorService.generateHtmlCode("做个计算器");
        Assert.notNull(result);
    }

    @Test
    void generateHCJCode() {
        String result = codeGeneratorService.generateHCJlCode("做个留言板");
    }
}
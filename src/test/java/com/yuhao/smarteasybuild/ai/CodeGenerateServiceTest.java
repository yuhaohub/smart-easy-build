package com.yuhao.smarteasybuild.ai;

import cn.hutool.core.lang.Assert;
import com.yuhao.smarteasybuild.ai.model.HCJCodeResult;
import com.yuhao.smarteasybuild.ai.model.HtmlCodeResult;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CodeGenerateServiceTest {
    @Resource
    CodeGeneratorService codeGeneratorService;

    @Test
    void generateHtmlCode() {
        HtmlCodeResult result = codeGeneratorService.generateHtmlCode("做个计算器");
        Assert.notNull(result);
    }

    @Test
    void generateHCJCode() {
        HCJCodeResult result = codeGeneratorService.generateHCJlCode("做个留言板");
    }
}
package com.yuhao.smarteasybuild.core;

import com.yuhao.smarteasybuild.model.enums.GenCodeTypeEnum;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;

@SpringBootTest
class AiCodeGeneratorFacadeTest {

    @Resource
    AiCodeGeneratorFacade aiCodeGeneratorFacade;

    @Test
    void generateHtmlCode(){
        File codeResult = aiCodeGeneratorFacade.generateCodeAndSave("简易记事本", GenCodeTypeEnum.HTML);
        Assertions.assertNotNull(codeResult);
    }
}
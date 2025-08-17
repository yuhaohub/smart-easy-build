package com.yuhao.smarteasybuild.core;

import com.yuhao.smarteasybuild.model.enums.GenCodeTypeEnum;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.io.File;
import java.util.List;

@SpringBootTest
class AiCodeGeneratorFacadeTest {

    @Resource
    AiCodeGeneratorFacade aiCodeGeneratorFacade;

    @Test
    void generateCode(){
        File result = aiCodeGeneratorFacade.generateCodeAndSave("简易记事本", GenCodeTypeEnum.HTML);

        Assertions.assertNotNull(result);
    }
    @Test
    void generateCodeStream(){
        Flux<String> codeStream = aiCodeGeneratorFacade.generateCodeAndSaveStream("简易记事本", GenCodeTypeEnum.HCJ);
        List<String> result = codeStream.collectList().block();
        Assertions.assertNotNull(result);
    }
}
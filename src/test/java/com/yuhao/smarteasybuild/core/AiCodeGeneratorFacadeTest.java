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
        File result = aiCodeGeneratorFacade.generateCodeAndSave("简易记事本", GenCodeTypeEnum.HTML,0L);

        Assertions.assertNotNull(result);
    }
    @Test
    void generateCodeStream(){
        Flux<String> codeStream = aiCodeGeneratorFacade.generateCodeAndSaveStream("简易记事本", GenCodeTypeEnum.HCJ,0L);
        List<String> result = codeStream.collectList().block();
        Assertions.assertNotNull(result);
    }

    @Test
    void generateVueProjectCodeStream() {
        Flux<String> codeStream = aiCodeGeneratorFacade.generateCodeAndSaveStream(
                "简单的任务记录网站，总代码量不超过 200 行",
                GenCodeTypeEnum.VUE_PROJECT, 1L);
        // 阻塞等待所有数据收集完成
        List<String> result = codeStream.collectList().block();
        // 验证结果
        Assertions.assertNotNull(result);
        String completeContent = String.join("", result);
        Assertions.assertNotNull(completeContent);
    }
}
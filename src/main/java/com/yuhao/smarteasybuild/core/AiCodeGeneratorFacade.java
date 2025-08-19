package com.yuhao.smarteasybuild.core;

import com.yuhao.smarteasybuild.ai.CodeGeneratorService;
import com.yuhao.smarteasybuild.ai.model.HCJCodeResult;
import com.yuhao.smarteasybuild.ai.model.HtmlCodeResult;
import com.yuhao.smarteasybuild.core.parser.CodeParserExecutor;
import com.yuhao.smarteasybuild.core.saver.CodeSaverExecutor;
import com.yuhao.smarteasybuild.exception.BusinessException;
import com.yuhao.smarteasybuild.exception.ErrorCode;
import com.yuhao.smarteasybuild.model.enums.GenCodeTypeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;

/**
 * AI代码生成门面类
 */
@Slf4j
@Service
public class AiCodeGeneratorFacade {
    @Resource
    private CodeGeneratorService codeGeneratorService;

    /**
     * 入口: 根据类型生成代码并保存
     * @param userMessage
     * @param genCodeType
     * @return
     */
    public File generateCodeAndSave(String userMessage, GenCodeTypeEnum genCodeType,Long appId){
        if (genCodeType == null){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"生成类型为空,请提供生成类型");
        }
        File result = null;
        switch (genCodeType){
            case HTML:
                HtmlCodeResult resultHtml = codeGeneratorService.generateHtmlCode(userMessage);
                result = CodeSaverExecutor.excute(resultHtml,genCodeType,appId);
                break;
            case HCJ:
                HCJCodeResult resultHCJ = codeGeneratorService.generateHCJlCode(userMessage);
                result = CodeSaverExecutor.excute(resultHCJ,genCodeType,appId);
                break;
            default:
                throw new BusinessException(ErrorCode.SYSTEM_ERROR,"未知的生成类型");
        }
        return result;
    }


    /**
     * 入口(流式):根据类型生成代码并保存
     * @param userMessage
     * @param genCodeType
     * @return
     */
    public  Flux<String> generateCodeAndSaveStream(String userMessage, GenCodeTypeEnum genCodeType, Long appId){
        if (genCodeType == null){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"生成类型为空,请提供生成类型");
        }
        Flux<String> result = null;
        switch (genCodeType){
            case HTML:
                result = codeGeneratorService.generateHtmlCodeStream(userMessage);
                return generateStreamProcess(result, genCodeType, appId);
            case HCJ:
                result = codeGeneratorService.generateHCJlCodeStream(userMessage);
                return generateStreamProcess(result, genCodeType, appId);
            default:
                throw new BusinessException(ErrorCode.SYSTEM_ERROR,"未知的生成类型");
        }
    }



    private  Flux<String> generateStreamProcess(Flux<String> codeStream, GenCodeTypeEnum genCodeType, Long appId){

        // 当流式返回生成代码完成后，再保存代码
        StringBuilder codeBuilder = new StringBuilder();
        return codeStream
                .doOnNext(chunk -> {
                    // 实时收集代码片段
                    codeBuilder.append(chunk);
                })
                .doOnComplete(() -> {
                    // 流式返回完成后保存代码
                    try {
                        String completeMultiFileCode = codeBuilder.toString();
                        Object parserResult = CodeParserExecutor.execute(completeMultiFileCode, genCodeType);
                        // 保存代码到文件
                        File savedDir = CodeSaverExecutor.excute(parserResult,genCodeType,appId);
                        log.info("保存成功，路径为：" + savedDir.getAbsolutePath());
                    } catch (Exception e) {
                        log.error("保存失败: {}", e.getMessage());
                    }
                });
    }

}

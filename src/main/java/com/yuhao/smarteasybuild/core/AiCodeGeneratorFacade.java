package com.yuhao.smarteasybuild.core;

import com.yuhao.smarteasybuild.ai.CodeGeneratorService;
import com.yuhao.smarteasybuild.ai.model.HCJCodeResult;
import com.yuhao.smarteasybuild.ai.model.HtmlCodeResult;
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
     * 入口
     * @param userMessage
     * @param genCodeTypeEnum
     * @return
     */
    public File generateCodeAndSave(String userMessage, GenCodeTypeEnum genCodeTypeEnum){
        if (genCodeTypeEnum == null){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"生成类型为空,请提供生成类型");
        }
        File result = null;
        switch (genCodeTypeEnum){
            case HTML:
                result = generateHtmlCodeAndSave(userMessage);
                break;
            case HCJ:
                result = generateHCJCodeAndSave(userMessage);
                break;
            default:
                throw new BusinessException(ErrorCode.SYSTEM_ERROR,"未知的生成类型");
        }
        return result;
    }

    /**
     * 生成并保存html文件
     * @param userMessage
     * @return
     */
    private File generateHtmlCodeAndSave(String userMessage){
        HtmlCodeResult htmlCodeResult = codeGeneratorService.generateHtmlCode(userMessage);
        return CodeFileSaver.saveHtmlCode(htmlCodeResult);
    }

    /**
     * 生成html+css+js文件并保存
     * @param userMessage
     * @return
     */
    private File generateHCJCodeAndSave(String userMessage){
        HCJCodeResult hcjCodeResult = codeGeneratorService.generateHCJlCode(userMessage);
        return CodeFileSaver.saveHtmlCssJsCode(hcjCodeResult);
    }


    /**
     * 入口(流式)
     * @param userMessage
     * @param genCodeTypeEnum
     * @return
     */
    public  Flux<String> generateCodeAndSaveStream(String userMessage, GenCodeTypeEnum genCodeTypeEnum){
        if (genCodeTypeEnum == null){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"生成类型为空,请提供生成类型");
        }
        Flux<String> result = null;
        switch (genCodeTypeEnum){
            case HTML:
                result = generateHtmlCodeAndSaveStream(userMessage);
                break;
            case HCJ:
                result = generateHCJCodeAndSaveStream(userMessage);
                break;
            default:
                throw new BusinessException(ErrorCode.SYSTEM_ERROR,"未知的生成类型");
        }
        return result;
    }

    /**
     * 生成并保存html文件(流式)
     * @param userMessage
     * @return
     */
    private Flux<String> generateHtmlCodeAndSaveStream(String userMessage){
        Flux<String> result = codeGeneratorService.generateHtmlCodeStream(userMessage);
        // 当流式返回生成代码完成后，再保存代码
        StringBuilder codeBuilder = new StringBuilder();
        return result
                .doOnNext(chunk -> {
                    // 实时收集代码片段
                    codeBuilder.append(chunk);
                })
                .doOnComplete(() -> {
                    // 流式返回完成后保存代码
                    try {
                        String completeHtmlCode = codeBuilder.toString();
                        HtmlCodeResult htmlCodeResult = CodeParser.parseHtmlCode(completeHtmlCode);
                        // 保存代码到文件
                        File savedDir = CodeFileSaver.saveHtmlCode(htmlCodeResult);
                        log.info("保存成功，路径为：" + savedDir.getAbsolutePath());
                    } catch (Exception e) {
                        log.error("保存失败: {}", e.getMessage());
                    }
                });
    }

    /**
     * 生成html+css+js文件并保存(流式)
     * @param userMessage
     * @return
     */
    private  Flux<String> generateHCJCodeAndSaveStream(String userMessage){
        Flux<String> result = codeGeneratorService.generateHCJlCodeStream(userMessage);
        // 当流式返回生成代码完成后，再保存代码
        StringBuilder codeBuilder = new StringBuilder();
        return result
                .doOnNext(chunk -> {
                    // 实时收集代码片段
                    codeBuilder.append(chunk);
                })
                .doOnComplete(() -> {
                    // 流式返回完成后保存代码
                    try {
                        String completeMultiFileCode = codeBuilder.toString();
                        HCJCodeResult multiFileResult = CodeParser.parseHCJCode(completeMultiFileCode);
                        // 保存代码到文件
                        File savedDir = CodeFileSaver.saveHtmlCssJsCode(multiFileResult);
                        log.info("保存成功，路径为：" + savedDir.getAbsolutePath());
                    } catch (Exception e) {
                        log.error("保存失败: {}", e.getMessage());
                    }
                });
    }


}

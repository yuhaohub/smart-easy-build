package com.yuhao.smarteasybuild.core;

import com.yuhao.smarteasybuild.ai.CodeGeneratorService;
import com.yuhao.smarteasybuild.ai.model.HCJCodeResult;
import com.yuhao.smarteasybuild.ai.model.HtmlCodeResult;
import com.yuhao.smarteasybuild.exception.BusinessException;
import com.yuhao.smarteasybuild.exception.ErrorCode;
import com.yuhao.smarteasybuild.model.enums.GenCodeTypeEnum;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * AI代码生成门面类
 */
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


}

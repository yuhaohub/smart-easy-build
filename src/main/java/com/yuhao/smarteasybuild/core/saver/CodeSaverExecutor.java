package com.yuhao.smarteasybuild.core.saver;

import com.yuhao.smarteasybuild.ai.model.HCJCodeResult;
import com.yuhao.smarteasybuild.ai.model.HtmlCodeResult;
import com.yuhao.smarteasybuild.exception.BusinessException;
import com.yuhao.smarteasybuild.exception.ErrorCode;
import com.yuhao.smarteasybuild.model.enums.GenCodeTypeEnum;

import java.io.File;

/**
 * 代码保存执行器
 */
public class CodeSaverExecutor {
    private static final HtmlCodeSaver htmlCodeSaver = new HtmlCodeSaver();

    private static final HCJCodeSaver hcjCodeSaver = new HCJCodeSaver();


    public static File excute(Object codeResult, GenCodeTypeEnum codeType){
        switch (codeType){
            case HTML:
                return htmlCodeSaver.save((HtmlCodeResult) codeResult);
            case HCJ:
                return hcjCodeSaver.save((HCJCodeResult) codeResult);
            default:
                throw new BusinessException(ErrorCode.SYSTEM_ERROR,"未知的生成类型");
        }
    }
}

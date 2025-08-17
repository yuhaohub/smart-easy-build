package com.yuhao.smarteasybuild.core.parser;

import com.yuhao.smarteasybuild.exception.BusinessException;
import com.yuhao.smarteasybuild.exception.ErrorCode;
import com.yuhao.smarteasybuild.model.enums.GenCodeTypeEnum;

/**
 * 代码解析执行器
 */
public class CodeParserExecutor {
    private static final HtmlCodeParser htmlCodeParser = new HtmlCodeParser();

    private static final HCJCodeParser hcjCodeParser = new HCJCodeParser();
    public static Object execute(String codeContent, GenCodeTypeEnum codeType){
        switch (codeType){
            case HTML:
                return htmlCodeParser.parse(codeContent);
            case HCJ:
                return hcjCodeParser.parse(codeContent);
            default:
                throw new BusinessException(ErrorCode.SYSTEM_ERROR,"未知的生成类型");
        }
    }
}

package com.yuhao.smarteasybuild.ai;

import com.yuhao.smarteasybuild.ai.model.HCJCodeResult;
import com.yuhao.smarteasybuild.ai.model.HtmlCodeResult;
import dev.langchain4j.service.SystemMessage;


public interface CodeGeneratorService {

    /**
     * 生成html页面
     * @param userMessage
     * @return
     */
    @SystemMessage(fromResource = "prompt/generate-html-system-prompt.txt")
    HtmlCodeResult generateHtmlCode(String userMessage);

    /**
     * 多文件 html+css+js
     * @param userMessage
     * @return
     */
    @SystemMessage(fromResource = "prompt/generate-hcj-system-prompt.txt")
    HCJCodeResult generateHCJlCode(String userMessage);
}

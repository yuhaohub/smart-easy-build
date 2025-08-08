package com.yuhao.smarteasybuild.ai;

import dev.langchain4j.service.SystemMessage;


public interface CodeGeneratorService {

    /**
     * 生成html页面
     * @param userMessage
     * @return
     */
    @SystemMessage(fromResource = "prompt/generate-html-system-prompt.txt")
    String generateHtmlCode(String userMessage);

    /**
     * 多文件 html+css+js
     * @param userMessage
     * @return
     */
    @SystemMessage(fromResource = "prompt/generate-hcj-system-prompt.txt")
    String generateHCJlCode(String userMessage);
}

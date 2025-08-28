package com.yuhao.smarteasybuild.ai;

import com.yuhao.smarteasybuild.ai.model.HCJCodeResult;
import com.yuhao.smarteasybuild.ai.model.HtmlCodeResult;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import reactor.core.publisher.Flux;


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

    /**
     * 生成html页面(流式)
     * @param userMessage
     * @return
     */
    @SystemMessage(fromResource = "prompt/generate-html-system-prompt.txt")
    Flux<String> generateHtmlCodeStream(String userMessage);

    /**
     * 多文件 html+css+js(流式)
     * @param userMessage
     * @return
     */
    @SystemMessage(fromResource = "prompt/generate-hcj-system-prompt.txt")
    Flux<String>  generateHCJlCodeStream(String userMessage);


    /**
     * 生成 Vue 项目代码（流式）
     * @param appId
     * @param userMessage
     * @return
     */
    @SystemMessage(fromResource = "prompt/generate-vue-system-prompt.txt")
    Flux<String> generateVueProjectCodeStream(@MemoryId Long appId, @UserMessage String userMessage);
}

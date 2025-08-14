package com.yuhao.smarteasybuild.ai.model;


import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

@Data
public class HCJCodeResult {

    @Description("html代码")
    private String htmlCode;

    @Description("css代码")
    private String cssCode;

    @Description("js代码")
    private String jsCode;

    @Description("生成的代码描述")
    private String description;
}

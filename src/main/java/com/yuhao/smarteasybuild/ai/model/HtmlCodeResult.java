package com.yuhao.smarteasybuild.ai.model;

import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

@Data
public class HtmlCodeResult {

    @Description("html代码")
    private String htmlCode;

    @Description("生成的代码描述")
    private String description;
}

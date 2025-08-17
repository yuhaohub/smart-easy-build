package com.yuhao.smarteasybuild.core.parser;

import com.yuhao.smarteasybuild.ai.model.HCJCodeResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HCJCodeParser implements CodeParser<HCJCodeResult>{
    private static final Pattern HTML_CODE_PATTERN = Pattern.compile("```html\\s*\\n([\\s\\S]*?)```", Pattern.CASE_INSENSITIVE);
    private static final Pattern CSS_CODE_PATTERN = Pattern.compile("```css\\s*\\n([\\s\\S]*?)```", Pattern.CASE_INSENSITIVE);
    private static final Pattern JS_CODE_PATTERN = Pattern.compile("```(?:js|javascript)\\s*\\n([\\s\\S]*?)```", Pattern.CASE_INSENSITIVE);
    @Override
    public HCJCodeResult parse(String content) {
        HCJCodeResult hcjCodeResult = new HCJCodeResult();
        String htmlCode = extractCodeByPattern(content, HTML_CODE_PATTERN);
        hcjCodeResult.setHtmlCode(htmlCode);
        String cssCode = extractCodeByPattern(content, CSS_CODE_PATTERN);
        hcjCodeResult.setCssCode(cssCode);
        String jsCode = extractCodeByPattern(content, JS_CODE_PATTERN);
        hcjCodeResult.setJsCode(jsCode);
        return hcjCodeResult;
    }
    /**
     * 根据正则表达式提取代码
     * @param content 原始内容
     * @param pattern 正则模式
     * @return
     */
    public String extractCodeByPattern(String content, Pattern pattern){
        if (content == null) {
            return null;
        }
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            String code = matcher.group(1);
            return code != null ? code.trim() : null; // 新增trim()
        }
        return null;
    }
}

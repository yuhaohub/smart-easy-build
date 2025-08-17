package com.yuhao.smarteasybuild.core.parser;

import com.yuhao.smarteasybuild.ai.model.HtmlCodeResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlCodeParser implements CodeParser<HtmlCodeResult>{
    private static final Pattern HTML_CODE_PATTERN = Pattern.compile("```html\\s*\\n([\\s\\S]*?)```", Pattern.CASE_INSENSITIVE);
    @Override
    public HtmlCodeResult parse(String content) {
        HtmlCodeResult htmlCodeResult = new HtmlCodeResult();
        String htmlCode = extractCodeByPattern(content, HTML_CODE_PATTERN);
        htmlCodeResult.setHtmlCode(htmlCode);
        return htmlCodeResult;
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

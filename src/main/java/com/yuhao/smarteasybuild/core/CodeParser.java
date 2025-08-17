package com.yuhao.smarteasybuild.core;

import com.yuhao.smarteasybuild.ai.model.HCJCodeResult;
import com.yuhao.smarteasybuild.ai.model.HtmlCodeResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 代码解析器
 */
@Deprecated
public class CodeParser {

    private static final Pattern HTML_CODE_PATTERN = Pattern.compile("```html\\s*\\n([\\s\\S]*?)```", Pattern.CASE_INSENSITIVE);
    private static final Pattern CSS_CODE_PATTERN = Pattern.compile("```css\\s*\\n([\\s\\S]*?)```", Pattern.CASE_INSENSITIVE);
    private static final Pattern JS_CODE_PATTERN = Pattern.compile("```(?:js|javascript)\\s*\\n([\\s\\S]*?)```", Pattern.CASE_INSENSITIVE);

    /**
     * 提取HTML文件代码
     * @param content
     * @return
     */
    public static HtmlCodeResult parseHtmlCode(String content){
        HtmlCodeResult htmlCodeResult = new HtmlCodeResult();
        String htmlCode = extractCodeByPattern(content, HTML_CODE_PATTERN);
        htmlCodeResult.setHtmlCode(htmlCode);
        return htmlCodeResult;
     }

    /**
     * 提取HTML、CSS、JS文件代码
     * @param content
     * @return
     */
     public static HCJCodeResult parseHCJCode(String content){
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
    public static String extractCodeByPattern(String content, Pattern pattern){
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

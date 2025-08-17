package com.yuhao.smarteasybuild.core;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.yuhao.smarteasybuild.ai.model.HCJCodeResult;
import com.yuhao.smarteasybuild.ai.model.HtmlCodeResult;
import com.yuhao.smarteasybuild.model.enums.GenCodeTypeEnum;

import java.io.File;
import java.nio.charset.StandardCharsets;

@Deprecated
public class CodeFileSaver {

    //文件根目录
    private static final String ROOT_DIR =  System.getProperty("user.dir") + "/tmp/code_output";

    //构建唯一路径

    private static String buildUniquePath(String bizType) {
        String uniqueDirName = StrUtil.format("{}_{}",bizType, IdUtil.getSnowflakeNextIdStr());
        String path = ROOT_DIR + File.separator +uniqueDirName;
        FileUtil.mkdir(path);
        return path;
    }

    //保存单个HTML页面代码
    public static File saveHtmlCode(HtmlCodeResult htmlCode) {
        String path = buildUniquePath(GenCodeTypeEnum.HTML.getValue());
        saveFile(path,"index.html",htmlCode.getHtmlCode());
        return new File(path);

    }

    //保存html+css+js文件代码
    public static File saveHtmlCssJsCode(HCJCodeResult htmlCode) {
        String path = buildUniquePath(GenCodeTypeEnum.HCJ.getValue());
        saveFile(path,"index.html",htmlCode.getHtmlCode());
        saveFile(path,"style.css",htmlCode.getCssCode());
        saveFile(path,"script.js",htmlCode.getJsCode());
        return new File(path);
    }


    //保存单个文件到本地
    public static void saveFile(String dir,String fileName, String fileContent) {
        FileUtil.writeString(fileContent, dir + File.separator + fileName, StandardCharsets.UTF_8);
    }
}

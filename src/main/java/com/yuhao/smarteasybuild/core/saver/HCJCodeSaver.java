package com.yuhao.smarteasybuild.core.saver;

import cn.hutool.core.util.StrUtil;
import com.yuhao.smarteasybuild.ai.model.HCJCodeResult;
import com.yuhao.smarteasybuild.exception.BusinessException;
import com.yuhao.smarteasybuild.exception.ErrorCode;
import com.yuhao.smarteasybuild.model.enums.GenCodeTypeEnum;

public class HCJCodeSaver extends CodeSaverTemplate<HCJCodeResult>{
    @Override
    protected String getFileType() {
        return GenCodeTypeEnum.HCJ.getValue();
    }

    @Override
    protected void saveFiles(HCJCodeResult result, String path) {
        saveFile(path,"index.html",result.getHtmlCode());
        saveFile(path,"style.css",result.getCssCode());
        saveFile(path,"script.js",result.getJsCode());
    }

    @Override
    protected void validateInput(HCJCodeResult result) {
        super.validateInput(result);
        if (result.getHtmlCode() == null || StrUtil.isBlank(result.getHtmlCode())) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Html代码结果不能为空");
        }
    }

}

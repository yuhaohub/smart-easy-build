package com.yuhao.smarteasybuild.core.saver;

import cn.hutool.core.util.StrUtil;
import com.yuhao.smarteasybuild.ai.model.HtmlCodeResult;
import com.yuhao.smarteasybuild.exception.BusinessException;
import com.yuhao.smarteasybuild.exception.ErrorCode;
import com.yuhao.smarteasybuild.model.enums.GenCodeTypeEnum;

public class HtmlCodeSaver extends CodeSaverTemplate<HtmlCodeResult>{
    @Override
    protected String getFileType() {
        return GenCodeTypeEnum.HTML.getValue();
    }

    @Override
    protected void saveFiles(HtmlCodeResult result, String path) {
            saveFile(path,"index.html",result.getHtmlCode());
    }


    @Override
    protected void validateInput(HtmlCodeResult result) {
        super.validateInput(result);
        if (StrUtil.isBlank(result.getHtmlCode())) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Html代码结果不能为空");
        }
    }
}

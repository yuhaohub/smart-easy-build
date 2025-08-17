package com.yuhao.smarteasybuild.core.saver;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.yuhao.smarteasybuild.exception.BusinessException;
import com.yuhao.smarteasybuild.exception.ErrorCode;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * 模版保存器
 */
public abstract class CodeSaverTemplate<T> {

    //文件根目录
    private static final String ROOT_DIR =  System.getProperty("user.dir") + "/tmp/code_output";

    protected File save(T codeResult){
        //校验
        validateInput(codeResult);
        //构建保存路径
        String path = buildUniquePath(getFileType());
        //保存代码文件
        saveFiles(codeResult,path);
        //返回文件对象
        return  new File(path);
    }


    /**
     * 构建唯一的保存路径
     * @param bizType
     * @return
     */
    protected  final String buildUniquePath(String bizType) {
        String uniqueDirName = StrUtil.format("{}_{}",bizType, IdUtil.getSnowflakeNextIdStr());
        String path = ROOT_DIR + File.separator +uniqueDirName;
        FileUtil.mkdir(path);
        return path;
    }
    //保存单个文件到本地
    protected  final void saveFile(String dir,String fileName, String fileContent) {
        FileUtil.writeString(fileContent, dir + File.separator + fileName, StandardCharsets.UTF_8);
    }

    /**
     * 校验
     * @param result
     */
    protected void validateInput(T result) {
        if (result == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "代码结果对象不能为空");
        }
    }

    /**
     * 获取文件类型（由子类实现）
     * @return
     */
    protected abstract String getFileType();

    /**
     * 保存文件的具体实现（由子类实现）
     *
     * @param result      代码结果对象
     * @param path        保存路径
     */
    protected abstract void saveFiles(T result, String path);
}

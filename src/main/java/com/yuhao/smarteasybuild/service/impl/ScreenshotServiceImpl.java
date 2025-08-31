package com.yuhao.smarteasybuild.service.impl;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.yuhao.smarteasybuild.exception.ErrorCode;
import com.yuhao.smarteasybuild.exception.ThrowUtils;
import com.yuhao.smarteasybuild.manager.CosManager;
import com.yuhao.smarteasybuild.service.ScreenshotService;
import com.yuhao.smarteasybuild.utils.WebScreenshotUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class ScreenshotServiceImpl implements ScreenshotService {

    @Resource
    private CosManager cosManager;

    @Override
    public String uploadScreenshot(String webUrl) {
        ThrowUtils.throwIf(StrUtil.isBlank(webUrl), ErrorCode.PARAMS_ERROR, "网页URL地址不能为空");
        log.info("开始网页截图");
        // 1、生成本地截图
        String localScreenshotPath = WebScreenshotUtils.saveWebPageScreenshot(webUrl);
        ThrowUtils.throwIf(StrUtil.isBlank(localScreenshotPath), ErrorCode.OPERATION_ERROR, "网页截图失败");

        // 2、上传到Cos
        String cosUrl = uploadScreenshotToCos(localScreenshotPath);
        ThrowUtils.throwIf(StrUtil.isBlank(cosUrl), ErrorCode.OPERATION_ERROR, "上传截图到Cos失败");
        log.info("网页截图上传Cos成功");
        // 3、清理本地截图文件
        cleanupLocalFile(localScreenshotPath);
        return cosUrl;
    }

    /**
     * 上传截图到Cos
     * @param localScreenshotPath
     * @return
     */
    private String uploadScreenshotToCos(String localScreenshotPath) {
        File screenshotFile = new File(localScreenshotPath);
        if (!screenshotFile.exists()) {
            log.error("截图文件不存在: {}", localScreenshotPath);
            return null;
        }
        // 生成 COS 对象键
        String fileName = UUID.randomUUID().toString().substring(0, 8) + "_compressed.jpg";
        String cosKey = generateScreenshotKey(fileName);
        return cosManager.uploadFile(cosKey, screenshotFile);
    }
    /**
     * 生成截图的对象存储键(应用名称 + 服务名称 + 日期 + 文件名)
     * 格式：seb/screenshots/2025/07/31/filename.jpg
     */
    private String generateScreenshotKey(String fileName) {
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        return String.format("seb/screenshots/%s/%s", datePath, fileName);
    }

    /**
     * 清理本地文件
     *
     * @param localFilePath 本地文件路径
     */
    private void cleanupLocalFile(String localFilePath) {
        File localFile = new File(localFilePath);
        if (localFile.exists()) {
            File parentDir = localFile.getParentFile();
            FileUtil.del(parentDir);
            log.info("本地截图文件已清理: {}", localFilePath);
        }
    }
}

package com.yuhao.smarteasybuild.ai.tools;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONObject;
import com.yuhao.smarteasybuild.constant.AppConstant;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolMemoryId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * 文件写入工具（供AI调用）
 */

@Slf4j
@Component
public class FileWriteTool extends BaseTool{

    @Tool("文件写入")
    public String writeFile(@P("文件相对路径") String relativePath, @P("待写入的文件内容") String content,@ToolMemoryId Long appId){
        Path path = Paths.get(relativePath);
        if (!path.isAbsolute()) {
            // 相对路径处理，创建基于 appId 的项目目录
            String projectDirName = "vue_project_" + appId;
            Path projectRoot = Paths.get(AppConstant.CODE_GEN_PATH, projectDirName);
            path = projectRoot.resolve(relativePath);
        }
        try {
            // 创建父目录（如果不存在）
            Path parentDir = path.getParent();
            if (parentDir != null) {
                Files.createDirectories(parentDir);
            }
            // 写入文件内容
            Files.write(path, content.getBytes(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
            log.info("成功写入文件: {}", path.toAbsolutePath());
            // 返回相对路径
            return "文件写入成功: " + relativePath;
        }catch (IOException e){
            String errorMessage = "文件写入失败: " + relativePath + ", 错误: " + e.getMessage();
            log.error(errorMessage, e);
            return errorMessage;
        }

    }

    @Override
    public String getToolName() {
        return "writeFile";
    }

    @Override
    public String getDisplayName() {
        return "文件写入";
    }

    @Override
    public String generateToolExecutedResult(JSONObject arguments) {
        String relativeFilePath = arguments.getStr("relativeFilePath");
        String suffix = FileUtil.getSuffix(relativeFilePath);
        String content = arguments.getStr("content");
        return String.format("""
                        [工具调用] %s %s
                        ```%s
                        %s
                        ```
                        """, getDisplayName(), relativeFilePath, suffix, content);
    }
}

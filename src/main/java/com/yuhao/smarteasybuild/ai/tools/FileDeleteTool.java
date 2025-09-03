package com.yuhao.smarteasybuild.ai.tools;

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

/**
 * 文件删除工具(供Ai调用)
 */
@Slf4j
@Component
public class FileDeleteTool extends BaseTool{
    @Tool("文件删除")
    public String deleteFile(@P("文件相对路径") String relativePath, @ToolMemoryId Long appId){
        Path path = Paths.get(relativePath);
        if (!path.isAbsolute()) {
            String projectDirName = "vue_project_" + appId;
            Path projectRoot = Paths.get(AppConstant.CODE_GEN_PATH, projectDirName);
            path = projectRoot.resolve(relativePath);
        }
        if (!Files.exists(path)) {
            return "警告：文件不存在，无需删除 - " + relativePath;
        }
        if (!Files.isRegularFile(path)) {
            return "错误：指定的路径不是文件，无法删除 - " + relativePath;
        }
        if (isImportantFile(path.getFileName().toString())) {
            return "警告：重要文件，不允许删除 - " + relativePath;
        }
        try {
            Files.delete(path);
            log.info("成功删除文件: {}", path.toAbsolutePath());
            return "文件删除成功: " + relativePath;
        } catch (IOException e) {
            String errorMessage = "删除文件失败: " + relativePath + ", 错误: " + e.getMessage();
            log.error(errorMessage, e);
            return errorMessage;
        }

    }
    /**
     * 判断是否是重要文件，不允许删除
     */
    private boolean isImportantFile(String fileName) {
        String[] importantFiles = {
                "package.json", "package-lock.json", "yarn.lock", "pnpm-lock.yaml",
                "vite.config.js", "vite.config.ts", "vue.config.js",
                "tsconfig.json", "tsconfig.app.json", "tsconfig.node.json",
                "index.html", "main.js", "main.ts", "App.vue", ".gitignore", "README.md"
        };
        for (String important : importantFiles) {
            if (important.equalsIgnoreCase(fileName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getToolName() {
        return "deleteFile";
    }

    @Override
    public String getDisplayName() {
        return "删除文件";
    }

    @Override
    public String generateToolExecutedResult(JSONObject arguments) {
        String relativeFilePath = arguments.getStr("relativeFilePath");
        return String.format("[工具调用] %s %s", getDisplayName(), relativeFilePath);
    }
}

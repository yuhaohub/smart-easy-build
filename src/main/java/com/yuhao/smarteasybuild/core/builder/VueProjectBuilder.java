package com.yuhao.smarteasybuild.core.builder;

import cn.hutool.core.util.RuntimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class VueProjectBuilder {

    /**
     * 异步构建项目（不阻塞主流程）
     *
     * @param projectPath 项目路径
     */
    public void buildProjectAsync(String projectPath) {
        // 在单独的线程中执行构建，避免阻塞主流程
        Thread.ofVirtual().name("vue-builder-" + System.currentTimeMillis()).start(() -> {
            try {
                buildVueProject(projectPath);
            } catch (Exception e) {
                log.error("异步构建 Vue 项目时发生异常: {}", e.getMessage(), e);
            }
        });
    }
    /**
     * 构建Vue项目
     * @param projectPath 项目路径
     * @return 是否构建成功
     */
    public boolean buildVueProject(String projectPath) {
        File projectDir = new File(projectPath);
        if (!projectDir.exists() || !projectDir.isDirectory()) {
            log.error("项目目录不存在: {}", projectPath);
            return false;
        }
        // 检查 package.json 是否存在
        File packageJson = new File(projectDir, "package.json");
        if (!packageJson.exists()) {
            log.error("package.json 文件不存在: {}", packageJson.getAbsolutePath());
            return false;
        }
        log.info("开始构建 Vue 项目: {}", projectPath);
        // 执行 npm install
        if (!executeNpmInstall(projectDir)) {
            log.error("npm install 执行失败");
            return false;
        }
        // 执行 npm run build
        if (!executeNpmBuild(projectDir)) {
            log.error("npm run build 执行失败");
            return false;
        }
        // 验证 dist 目录是否生成
        File distDir = new File(projectDir, "dist");
        if (!distDir.exists() || distDir.list().length == 0) { // 检查是否存在且非空
            log.error("构建完成但 dist 目录不存在或为空: {}", distDir.getAbsolutePath());
            return false;
        }
        log.info("Vue 项目构建成功，dist 目录: {}", distDir.getAbsolutePath());
        return true;

    }
    /**
     * 执行命令
     * @param workingDir 工作路径
     * @param command 待执行命令
     * @param timeoutSeconds 超时时间
     * @return
     */
    private boolean executeCommand(File workingDir, String command, int timeoutSeconds) {
        log.info("在目录 {} 中执行命令: {}", workingDir.getAbsolutePath(), command);
        Process process = RuntimeUtil.exec(null, workingDir, command.split("\\s+"));

        // 异步读取输出流和错误流，避免缓冲区阻塞
        new Thread(() -> {
            try (var reader = new java.io.BufferedReader(new java.io.InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    log.info("[stdout] {}", line);
                }
            } catch (Exception e) {
                log.error("读取 stdout 失败: {}", e.getMessage());
            }
        }).start();

        new Thread(() -> {
            try (var reader = new java.io.BufferedReader(new java.io.InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    log.error("[stderr] {}", line);
                }
            } catch (Exception e) {
                log.error("读取 stderr 失败: {}", e.getMessage());
            }
        }).start();

        try {
            boolean isFinished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
            if (!isFinished) {
                log.error("命令执行超时 {} 秒，终止进程", timeoutSeconds);
                process.destroyForcibly();
                return false;
            }
        } catch (InterruptedException e) {
            log.error("执行命令失败: {}, 错误信息: {}", command, e.getMessage());
            Thread.currentThread().interrupt();
            return false;
        }
        int exitCode = process.exitValue();
        if (exitCode == 0) {
            log.info("命令执行成功: {}", command);
            return true;
        } else {
            log.error("命令执行失败，退出码: {}", exitCode);
            return false;
        }
    }

    /**
     * 执行 npm install 命令
     */
    private boolean executeNpmInstall(File projectDir) {
        log.info("执行 npm install...");
        String command = String.format("%s install",System.getProperty("os.name").toLowerCase().contains("windows") ? "npm.cmd" : "npm");
        return executeCommand(projectDir, command, 300); // 5分钟超时
    }

    /**
     * 执行 npm run build 命令
     */
    private boolean executeNpmBuild(File projectDir) {
        log.info("执行 npm run build...");
        String command = String.format("%s run build",System.getProperty("os.name").toLowerCase().contains("windows") ? "npm.cmd" : "npm");
        return executeCommand(projectDir, command, 180); // 3分钟超时
    }
}

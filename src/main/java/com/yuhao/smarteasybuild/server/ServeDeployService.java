package com.yuhao.smarteasybuild.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.DisposableBean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

@Service
public class ServeDeployService implements DisposableBean {
    
    private static final Logger log = LoggerFactory.getLogger(ServeDeployService.class);
    private static final String CODE_BASE_DIR = "/tmp/code_deploy";
    private static final int SERVE_PORT = 3000;
    private Process serveProcess;
    
    /**
     * 启动 Serve 服务
     */
    public synchronized void startServeService() { // 增加同步，避免并发启动
        try {
            if (isProcessAlive()) {
                log.info("Serve service is already running on port {}", SERVE_PORT);
                return;
            }
            
            ProcessBuilder pb = new ProcessBuilder(
                "npx", "serve", CODE_BASE_DIR, "-p", String.valueOf(SERVE_PORT)
            );
            pb.redirectErrorStream(true); // 合并错误流到输出流
            serveProcess = pb.start();
            
            // 启动线程读取进程输出，避免缓冲区阻塞
            readProcessOutput(serveProcess.getInputStream());
            
            // 简单判断是否启动成功
            Thread.sleep(1000); // 等待服务初始化
            if (isProcessAlive()) {
                log.info("Serve service started successfully on port {}", SERVE_PORT);
            } else {
                log.error("Serve service failed to start (process exited immediately)");
            }
        } catch (Exception e) {
            log.error("Failed to start serve service", e);
            throw new RuntimeException("Failed to start serve service", e);
        }
    }
    
    /**
     * 关闭 Serve 服务
     */
    public synchronized void stopServeService() { // 增加同步，避免并发停止
        if (!isProcessAlive()) {
            log.info("Serve service is not running");
            return;
        }
        
        try {
            serveProcess.destroy();
            // 等待进程优雅退出
            boolean terminated = serveProcess.waitFor(5, TimeUnit.SECONDS);
            if (terminated) {
                log.info("Serve service stopped normally, exit code: {}", serveProcess.exitValue());
            } else {
                // 强制终止
                serveProcess.destroyForcibly();
                log.warn("Serve service was force stopped");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // 恢复中断状态
            serveProcess.destroyForcibly();
            log.warn("Interrupted while stopping serve service, force stopped", e);
        } finally {
            serveProcess = null; // 清空引用
        }
    }
    
    /**
     * 读取进程输出流（避免阻塞）
     */
    private void readProcessOutput(InputStream inputStream) {
        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    log.info("Serve output: {}", line); // 输出进程日志
                }
            } catch (IOException e) {
                log.error("Error reading serve process output", e);
            }
        }, "serve-output-reader").start(); // 命名线程，便于排查
    }
    
    /**
     * 判断进程是否存活
     */
    private boolean isProcessAlive() {
        return serveProcess != null && serveProcess.isAlive();
    }
    
    /**
     * 容器销毁时停止服务（确保资源释放）
     */
    @Override
    public void destroy() {
        stopServeService();
    }
}

package com.yuhao.smarteasybuild.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import javax.annotation.PreDestroy;

//@Component
public class ServeLifecycleManager {

    private static final Logger log = LoggerFactory.getLogger(ServeLifecycleManager.class);

    @Autowired
    private ServeDeployService serveDeployService;

    /**
     * 监听Spring Boot应用就绪事件，在应用完全启动后启动Serve服务
     * ApplicationReadyEvent触发时，所有Bean已初始化，Web服务器已启动
     */
    @EventListener(ApplicationReadyEvent.class)
    public void startServeOnApplicationReady() {
        try {
            log.info("Starting Serve service as application is ready...");
            serveDeployService.startServeService();
            log.info("Serve service startup triggered successfully");
        } catch (Exception e) {
            log.error("Failed to start Serve service during application initialization", e);

        }
    }

    /**
     * 应用关闭前（Spring容器销毁时）停止Serve服务
     * 执行时机早于DisposableBean的destroy()方法
     */
    @PreDestroy
    public void stopServeOnApplicationShutdown() {
        log.info("Shutting down Serve service as application is closing...");
        try {
            serveDeployService.stopServeService();
            log.info("Serve service shutdown completed");
        } catch (Exception e) {
            log.error("Error occurred while stopping Serve service", e);
        }
    }
}
    
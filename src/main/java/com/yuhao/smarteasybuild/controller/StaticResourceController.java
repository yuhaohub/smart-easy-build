package com.yuhao.smarteasybuild.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@RestController
@RequestMapping("/static")
public class StaticResourceController {
    private static final Logger log = LoggerFactory.getLogger(StaticResourceController.class);
    // 应用生成根目录（用于浏览）
    private static final String PREVIEW_ROOT_DIR = System.getProperty("user.dir") + "/tmp/code_output";
    // 日期格式化（用于Last-Modified头）
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
            "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);

    /**
     * 提供静态资源访问，支持目录重定向和安全校验
     * 访问格式：http://localhost:8123/api/static/{deployKey}[/{fileName}]
     */
    @GetMapping("/{deployKey}/**")
    public ResponseEntity<Resource> serveStaticResource(
            @PathVariable String deployKey,
            HttpServletRequest request) {
        try {
            // 获取资源路径
            String resourcePath = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
            resourcePath = resourcePath.substring(("/static/" + deployKey).length());

            // 目录访问处理（重定向带斜杠）
            if (resourcePath.isEmpty()) {
                HttpHeaders headers = new HttpHeaders();
                headers.add("Location", request.getRequestURI() + "/");
                return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
            }

            // 默认首页处理
            if (resourcePath.equals("/")) {
                resourcePath = "/index.html";
            }

            // 安全的路径处理（防止目录遍历攻击）
            Path rootPath = Paths.get(PREVIEW_ROOT_DIR).resolve(deployKey).toAbsolutePath().normalize();
            Path resourcePathObj = Paths.get(resourcePath.startsWith("/") ? resourcePath.substring(1) : resourcePath);
            Path targetPath = rootPath.resolve(resourcePathObj).normalize();

            // 路径越界检查
            if (!targetPath.startsWith(rootPath)) {
                log.warn("路径越界访问尝试，deployKey: {}, 请求路径: {}", deployKey, resourcePath);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            // 符号链接检查
            if (Files.isSymbolicLink(targetPath)) {
                log.warn("符号链接访问被拒绝，deployKey: {}, 请求路径: {}", deployKey, resourcePath);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            File file = targetPath.toFile();
            // 文件存在性检查
            if (!file.exists()) {
                log.debug("资源不存在，deployKey: {}, 请求路径: {}", deployKey, resourcePath);
                return ResponseEntity.notFound().build();
            }

            // 目录访问拒绝
            if (file.isDirectory()) {
                log.debug("目录访问被拒绝，deployKey: {}, 请求路径: {}", deployKey, resourcePath);
                return ResponseEntity.notFound().build();
            }

            // 构建响应
            Resource resource = new FileSystemResource(file);
            return ResponseEntity.ok()
                    .header("Content-Type", getContentTypeWithCharset(targetPath.toString()))
                    .header("Cache-Control", getCacheControlHeader(targetPath.toString()))
                    .header("Last-Modified", DATE_FORMAT.format(new Date(file.lastModified())))
                    .body(resource);

        } catch (Exception e) {
            log.error("处理静态资源访问异常，deployKey: {}", deployKey, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 根据文件扩展名返回带字符编码的Content-Type
     */
    private String getContentTypeWithCharset(String filePath) {
        String extension = getFileExtension(filePath).toLowerCase();

        // 常见类型手动映射（优先级高）
        switch (extension) {
            case "html": return "text/html; charset=UTF-8";
            case "css": return "text/css; charset=UTF-8";
            case "js": return "application/javascript; charset=UTF-8";
            case "json": return "application/json; charset=UTF-8";
            case "xml": return "text/xml; charset=UTF-8";
            case "svg": return "image/svg+xml; charset=UTF-8";
            case "txt": return "text/plain; charset=UTF-8";
            case "png": return "image/png";
            case "jpg": case "jpeg": return "image/jpeg";
            case "gif": return "image/gif";
            case "ico": return "image/x-icon";
            case "pdf": return "application/pdf";
            case "zip": return "application/zip";
            default:
                // 自动探测其他类型
                try {
                    String probeType = Files.probeContentType(Paths.get(filePath));
                    return probeType != null ? probeType : "application/octet-stream";
                } catch (IOException e) {
                    log.debug("无法探测文件类型，路径: {}", filePath, e);
                    return "application/octet-stream";
                }
        }
    }

    /**
     * 根据文件类型返回缓存策略
     */
    private String getCacheControlHeader(String filePath) {
        String extension = getFileExtension(filePath).toLowerCase();

        // 静态资源设置较长缓存，HTML等动态资源设置短缓存
        if (extension.matches("(html|htm)")) {
            return "max-age=60, public"; // HTML缓存1分钟
        } else if (extension.matches("(js|css|png|jpg|jpeg|gif|svg|ico)")) {
            return "max-age=86400, public"; // 静态资源缓存1天
        }
        return "max-age=3600, public"; // 其他资源缓存1小时
    }

    /**
     * 提取文件扩展名
     */
    private String getFileExtension(String filePath) {
        int lastDotIndex = filePath.lastIndexOf('.');
        return lastDotIndex > 0 && lastDotIndex < filePath.length() - 1
                ? filePath.substring(lastDotIndex + 1)
                : "";
    }
}

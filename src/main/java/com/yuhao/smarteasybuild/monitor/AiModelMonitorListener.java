package com.yuhao.smarteasybuild.monitor;

import dev.langchain4j.model.chat.listener.ChatModelErrorContext;
import dev.langchain4j.model.chat.listener.ChatModelListener;
import dev.langchain4j.model.chat.listener.ChatModelRequestContext;
import dev.langchain4j.model.chat.listener.ChatModelResponseContext;
import dev.langchain4j.model.output.TokenUsage;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

@Component
public class AiModelMonitorListener implements ChatModelListener {

    private  static final  String REQUEST_START_TIME_KEY = "request_start_time";
    private static final String MONITOR_CONTEXT_KEY = "monitor_context";

    @Resource
    private AiModelMetricsCollector aiMetricsCollector;

    @Override
    public void onError(ChatModelErrorContext errorContext) {
        // 从监控上下文中获取信息
        MonitorContext context = MonitorContextHolder.getContext();
        String userId = context.getUserId();
        String appId = context.getAppId();
        // 模型信息
        String modelName = errorContext.chatRequest().modelName();
        String errorMsg = errorContext.error().getMessage();
        // 记录
        aiMetricsCollector.recordRequest(userId,appId,modelName,"error");
        aiMetricsCollector.recordError(userId,appId,modelName,errorMsg);

    }

    @Override
    public void onResponse(ChatModelResponseContext responseContext) {
        // 从属性中获取监控信息（由 onRequest 方法存储）
        Map<Object, Object> attributes = responseContext.attributes();
        // 从监控上下文中获取信息
        MonitorContext context = (MonitorContext) attributes.get(MONITOR_CONTEXT_KEY);
        String userId = context.getUserId();
        String appId = context.getAppId();
        // 获取模型名称
        String modelName = responseContext.chatResponse().modelName();
        // 记录成功请求
        aiMetricsCollector.recordRequest(userId, appId, modelName, "success");
        // 记录响应时间
        recordResponseTime(attributes, userId, appId, modelName);
        // 记录 Token 使用情况
        recordTokenUsage(responseContext, userId, appId, modelName);
    }

    @Override
    public void onRequest(ChatModelRequestContext requestContext) {
        // 记录请求开始时间
        requestContext.attributes().put(REQUEST_START_TIME_KEY, Instant.now());
        // 从监控上下文中获取信息
        MonitorContext context = MonitorContextHolder.getContext();
        String userId = context.getUserId();
        String appId = context.getAppId();
        requestContext.attributes().put(MONITOR_CONTEXT_KEY, context);
        // 获取模型名称
        String modelName = requestContext.chatRequest().modelName();
        // 记录请求指标
        aiMetricsCollector.recordRequest(userId, appId, modelName, "start");
    }
    /**
     * 记录响应时间
     */
    private void recordResponseTime(Map<Object, Object> attributes, String userId, String appId, String modelName) {
        Instant startTime = (Instant) attributes.get(REQUEST_START_TIME_KEY);
        Duration responseTime = Duration.between(startTime, Instant.now());
        aiMetricsCollector.recordResponseTime(userId, appId, modelName, responseTime);
    }

    /**
     * 记录Token使用情况
     */
    private void recordTokenUsage(ChatModelResponseContext responseContext, String userId, String appId, String modelName) {
        TokenUsage tokenUsage = responseContext.chatResponse().metadata().tokenUsage();
        if (tokenUsage != null) {
            aiMetricsCollector.recordTokenUsage(userId, appId, modelName, "input", tokenUsage.inputTokenCount());
            aiMetricsCollector.recordTokenUsage(userId, appId, modelName, "output", tokenUsage.outputTokenCount());
            aiMetricsCollector.recordTokenUsage(userId, appId, modelName, "total", tokenUsage.totalTokenCount());
        }
    }
}

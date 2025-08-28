package com.yuhao.smarteasybuild.core.handler;

import com.yuhao.smarteasybuild.model.entity.User;
import com.yuhao.smarteasybuild.model.enums.ChatHistoryMessageTypeEnum;
import com.yuhao.smarteasybuild.service.ChatHistoryService;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

/**
 * 简单文本流处理器
 * 处理 HTML 和 HCJ 类型的流式响应
 */
@Slf4j
public class SimpleTextStreamHandler {

    /**
     * 处理传统流（HTML, HCJ）
     * 直接收集完整的文本响应
     *
     * @param originFlux         原始流
     * @param chatHistoryService 聊天历史服务
     * @param appId              应用ID
     * @param loginUser          登录用户
     * @return 处理后的流
     */
    public Flux<String> handle(Flux<String> originFlux,
                               ChatHistoryService chatHistoryService,
                               long appId, User loginUser) {
        StringBuilder aiResponseBuilder = new StringBuilder();
        return originFlux
                .map(chunk -> {
                    // 收集AI响应内容
                    aiResponseBuilder.append(chunk);
                    return chunk;
                })
                .doOnComplete(() -> {
                    // 流式响应完成后，添加AI消息到对话历史
                    String aiResponse = aiResponseBuilder.toString();
                    chatHistoryService.saveMessage(appId, loginUser.getId(), aiResponse, ChatHistoryMessageTypeEnum.AI.getValue() );
                })
                .doOnError(error -> {
                    // AI回复失败，记录错误消息
                    String errorMessage = "AI回复失败: " + error.getMessage();
                    chatHistoryService.saveMessage(appId, loginUser.getId(), errorMessage, ChatHistoryMessageTypeEnum.AI.getValue() );
                });
    }
}
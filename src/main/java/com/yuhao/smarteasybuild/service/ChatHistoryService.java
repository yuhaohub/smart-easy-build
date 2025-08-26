package com.yuhao.smarteasybuild.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yuhao.smarteasybuild.model.entity.ChatHistory;
import com.yuhao.smarteasybuild.model.entity.User;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;

import java.time.LocalDateTime;

/**
* @author hyh
* @description 针对表【chat_history(对话历史)】的数据库操作Service
* @createDate 2025-08-24 15:53:55
*/
public interface ChatHistoryService extends IService<ChatHistory> {
    /**
     *
     *  保存对话历史
     * @param appId
     * @param userId
     * @param message
     * @param messageType 用户/AI
     * @return
     */
    boolean saveMessage(Long appId, Long userId, String message, String messageType);

    /**
     * 根据appId删除对话历史
     * @param appId
     * @return
     */
    boolean deleteByAppId(Long appId);

    /**
     * 分页查询对话历史(游标查询)
     * @param appId
     * @param loginuser
     * @param pageSize
     * @param lastTime
     * @return
     */
    Page<ChatHistory> listAppChatHistoryByPage(Long appId, User loginuser, int pageSize, LocalDateTime lastTime);

    /**
     * 加载对话历史到内存中
     * @param appId
     * @param chatMemory
     * @param maxCount
     * @return
     */
    int loadChatHistoryToMemory(Long appId, MessageWindowChatMemory chatMemory, int maxCount);
}

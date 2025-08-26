package com.yuhao.smarteasybuild.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuhao.smarteasybuild.constant.UserConstant;
import com.yuhao.smarteasybuild.exception.ErrorCode;
import com.yuhao.smarteasybuild.exception.ThrowUtils;
import com.yuhao.smarteasybuild.mapper.ChatHistoryMapper;
import com.yuhao.smarteasybuild.model.entity.App;
import com.yuhao.smarteasybuild.model.entity.ChatHistory;
import com.yuhao.smarteasybuild.model.entity.User;
import com.yuhao.smarteasybuild.model.enums.ChatHistoryMessageTypeEnum;
import com.yuhao.smarteasybuild.service.ChatHistoryService;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
* @author hyh
* @description 针对表【chat_history(对话历史)】的数据库操作Service实现
* @createDate 2025-08-24 15:53:55
*/
@Slf4j
@Service
public class ChatHistoryServiceImpl extends ServiceImpl<ChatHistoryMapper, ChatHistory>
    implements ChatHistoryService{
    @Lazy
    @Resource
    private AppServiceImpl appService;

    @Override
    public boolean saveMessage(Long appId, Long userId, String message, String messageType) {
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用ID无效");
        ThrowUtils.throwIf(userId == null || userId <= 0, ErrorCode.PARAMS_ERROR, "用户ID无效");
        ThrowUtils.throwIf(StrUtil.isBlank(message), ErrorCode.PARAMS_ERROR, "消息不能为空");
        ThrowUtils.throwIf(StrUtil.isBlank(messageType), ErrorCode.PARAMS_ERROR, "消息类型不能为空");
        //验证消息类型
        ChatHistoryMessageTypeEnum messageTypeEnum = ChatHistoryMessageTypeEnum.getEnumByValue(messageType);
        ThrowUtils.throwIf(messageTypeEnum == null, ErrorCode.PARAMS_ERROR, "消息类型无效");
        ChatHistory chatHistory = ChatHistory.builder()
                .appId(appId)
                .userId(userId)
                .message(message)
                .messageType(messageTypeEnum.getValue())
                .build();
        return this.save(chatHistory);
    }

    @Override
    public boolean deleteByAppId(Long appId) {
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用ID无效");
        QueryWrapper<ChatHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("appId", appId);
        return this.remove(queryWrapper);
    }

    @Override
    public Page<ChatHistory> listAppChatHistoryByPage(Long appId, User loginUser, int pageSize, LocalDateTime lastTime) {
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用ID无效");
        ThrowUtils.throwIf(pageSize <= 0 || pageSize > 50, ErrorCode.PARAMS_ERROR, "页面大小必须在1-50之间");
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        // 验证权限：只有应用创建者和管理员可以查看
        App app = appService.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        boolean isAdmin = UserConstant.ADMIN_ROLE.equals(loginUser.getUserRole());
        boolean isCreator = app.getUserId().equals(loginUser.getId());
        ThrowUtils.throwIf(!isAdmin && !isCreator, ErrorCode.NO_AUTH_ERROR, "无权查看该应用的对话历史");
        QueryWrapper<ChatHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("appId", appId);
        if (lastTime != null) {
            queryWrapper.lt("createTime", lastTime);
        }
        return this.page(Page.of(1, pageSize), queryWrapper);
    }

    @Override
    public int loadChatHistoryToMemory(Long appId, MessageWindowChatMemory chatMemory, int maxCount) {
        try {
            // 构造查询条件，起始点为 1 而不是 0，用于排除最新的用户消息
            QueryWrapper<ChatHistory> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("appId", appId);
            queryWrapper.orderByDesc("createTime");
            queryWrapper.last("LIMIT 1," + maxCount);
            List<ChatHistory> historyList = this.list(queryWrapper);
            if (CollUtil.isEmpty(historyList)) {
                return 0;
            }
            // 反转列表，确保按时间正序（老的在前，新的在后）
            historyList = historyList.reversed();
            // 按时间顺序添加到记忆中
            int loadedCount = 0;
            // 先清理历史缓存，防止重复加载
            chatMemory.clear();
            for (ChatHistory history : historyList) {
                if (ChatHistoryMessageTypeEnum.USER.getValue().equals(history.getMessageType())) {
                    chatMemory.add(UserMessage.from(history.getMessage()));
                    loadedCount++;
                } else if (ChatHistoryMessageTypeEnum.AI.getValue().equals(history.getMessageType())) {
                    chatMemory.add(AiMessage.from(history.getMessage()));
                    loadedCount++;
                }
            }
            log.info("成功为 appId: {} 加载了 {} 条历史对话", appId, loadedCount);
            return loadedCount;
        } catch (Exception e) {
            log.error("加载历史对话失败，appId: {}, error: {}", appId, e.getMessage(), e);
            // 加载失败不影响系统运行，只是没有历史上下文
            return 0;
        }
    }
}





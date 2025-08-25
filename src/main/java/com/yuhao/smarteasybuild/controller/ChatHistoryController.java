package com.yuhao.smarteasybuild.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yuhao.smarteasybuild.common.BaseResponse;
import com.yuhao.smarteasybuild.common.ResultUtils;
import com.yuhao.smarteasybuild.model.entity.ChatHistory;
import com.yuhao.smarteasybuild.model.entity.User;
import com.yuhao.smarteasybuild.service.ChatHistoryService;
import com.yuhao.smarteasybuild.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("history")
public class ChatHistoryController {
    @Resource
    private ChatHistoryService chatHistoryService;
    @Resource
    private UserService userService;
    /**
     * 分页查询某个应用的对话历史（游标查询）
     *
     * @param appId          应用ID
     * @param pageSize       页面大小
     * @param lastCreateTime 最后一条对话的创建时间
     * @param request
     * @return 对话历史分页
     */
    @GetMapping("/app/{appId}")
    public BaseResponse<Page<ChatHistory>> listAppChatHistory(@PathVariable Long appId,
                                                              @RequestParam(defaultValue = "10") int pageSize,
                                                              @RequestParam(required = false) LocalDateTime lastCreateTime,
                                                              HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        Page<ChatHistory> result = chatHistoryService.listAppChatHistoryByPage(appId, loginUser, pageSize, lastCreateTime);
        return ResultUtils.success(result);
    }


}

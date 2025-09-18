package com.yuhao.smarteasybuild.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuhao.smarteasybuild.constant.AppConstant;
import com.yuhao.smarteasybuild.core.AiCodeGeneratorFacade;
import com.yuhao.smarteasybuild.core.builder.VueProjectBuilder;
import com.yuhao.smarteasybuild.core.handler.StreamHandlerExecutor;
import com.yuhao.smarteasybuild.exception.BusinessException;
import com.yuhao.smarteasybuild.exception.ErrorCode;
import com.yuhao.smarteasybuild.exception.ThrowUtils;
import com.yuhao.smarteasybuild.mapper.AppMapper;
import com.yuhao.smarteasybuild.model.dto.app.AppQueryRequest;
import com.yuhao.smarteasybuild.model.entity.App;
import com.yuhao.smarteasybuild.model.entity.User;
import com.yuhao.smarteasybuild.model.enums.ChatHistoryMessageTypeEnum;
import com.yuhao.smarteasybuild.model.enums.GenCodeTypeEnum;
import com.yuhao.smarteasybuild.model.vo.AppVO;
import com.yuhao.smarteasybuild.model.vo.UserVO;
import com.yuhao.smarteasybuild.monitor.MonitorContext;
import com.yuhao.smarteasybuild.monitor.MonitorContextHolder;
import com.yuhao.smarteasybuild.service.AppService;
import com.yuhao.smarteasybuild.service.ChatHistoryService;
import com.yuhao.smarteasybuild.service.ScreenshotService;
import com.yuhao.smarteasybuild.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author hyh
 * @description 针对表【app(应用表)】的数据库操作Service实现
 * @createDate 2025-08-17 21:43:57
 */
@Service
@Slf4j
public class AppServiceImpl extends ServiceImpl<AppMapper, App>
        implements AppService {
    @Resource
    private AiCodeGeneratorFacade aiCodeGeneratorFacade;
    @Resource
    private UserService userService;
    @Resource
    private ChatHistoryService chatHistoryService;
    @Resource
    private StreamHandlerExecutor streamHandlerExecutor;
    @Resource
    private VueProjectBuilder vueProjectBuilder;
    @Resource
    private ScreenshotService screenshotService;

    @Override
    public AppVO getAppVO(App app) {
        if (app == null) {
            return null;
        }
        AppVO appVO = new AppVO();
        BeanUtil.copyProperties(app, appVO);
        // 关联查询用户信息
        Long userId = app.getUserId();
        if (userId != null) {
            User user = userService.getById(userId);
            UserVO userVO = userService.getUserVO(user);
            appVO.setUser(userVO);
        }
        return appVO;
    }

    @Override
    public QueryWrapper<App> getQueryWrapper(AppQueryRequest appQueryRequest) {
        if (appQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = appQueryRequest.getId();
        String appName = appQueryRequest.getAppName();
        String cover = appQueryRequest.getCover();
        String initPrompt = appQueryRequest.getInitPrompt();
        String codeGenType = appQueryRequest.getCodeGenType();
        String deployKey = appQueryRequest.getDeployKey();
        Integer priority = appQueryRequest.getPriority();
        Long userId = appQueryRequest.getUserId();
        String sortField = appQueryRequest.getSortField();
        String sortOrder = appQueryRequest.getSortOrder();
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjUtil.isNotNull(id), "id", id);
        queryWrapper.like(StrUtil.isNotBlank(appName), "appName", appName);
        queryWrapper.like(StrUtil.isNotBlank(cover), "cover", cover);
        queryWrapper.like(StrUtil.isNotBlank(initPrompt), "initPrompt", initPrompt);
        queryWrapper.eq(StrUtil.isNotBlank(codeGenType), "codeGenType", codeGenType);
        queryWrapper.like(StrUtil.isNotBlank(deployKey), "deployKey", deployKey);
        queryWrapper.eq(ObjUtil.isNotNull(priority), "priority", priority);
        queryWrapper.eq(ObjUtil.isNotNull(userId), "userId", userId);
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;

    }

    @Override
    public List<AppVO> getAppVOList(List<App> appList) {
        if (CollUtil.isEmpty(appList)) {
            return new ArrayList<>();
        }
        // 批量获取用户信息，避免 N+1 查询问题
        Set<Long> userIds = appList.stream()
                .map(App::getUserId)
                .collect(Collectors.toSet());
        Map<Long, UserVO> userVOMap = userService.listByIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, userService::getUserVO));
        return appList.stream().map(app -> {
            AppVO appVO = getAppVO(app);
            UserVO userVO = userVOMap.get(app.getUserId());
            appVO.setUser(userVO);
            return appVO;
        }).collect(Collectors.toList());
    }


    @Override
    public Flux<String> chatToGenCode(Long appId, String message, User loginUser) {
        // 1. 参数校验
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用 ID 不能为空");
        ThrowUtils.throwIf(StrUtil.isBlank(message), ErrorCode.PARAMS_ERROR, "用户消息不能为空");
        // 2. 查询应用信息
        App app = this.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        // 3. 验证用户是否有权限访问该应用，仅本人可以生成代码
        if (!app.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限访问该应用");
        }
        // 4. 获取应用的代码生成类型
        String codeGenTypeStr = app.getCodeGenType();
        GenCodeTypeEnum codeGenTypeEnum = GenCodeTypeEnum.getEnumByValue(codeGenTypeStr);
        if (codeGenTypeEnum == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的代码生成类型");
        }
        // 保存用户消息到对话历史
        chatHistoryService.saveMessage(appId, loginUser.getId(), message, ChatHistoryMessageTypeEnum.USER.getValue());
        // 设置监控上下文
        MonitorContextHolder.setContext(MonitorContext.builder().userId(loginUser.getId().toString()).appId(appId.toString()).build());
        // 5. 调用 AI 生成代码
        Flux<String> contentFlux = aiCodeGeneratorFacade.generateCodeAndSaveStream(message, codeGenTypeEnum, appId);

        return streamHandlerExecutor.doExecute(contentFlux, chatHistoryService, appId, loginUser, codeGenTypeEnum).doFinally(
                signalType -> {
                    MonitorContextHolder.clearContext();
                }
        );
    }

    @Override
    public String deployApp(Long appId, User loginUser) {
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用 ID 不能为空");
        //获取应用信息
        App app = this.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        //权限校验
        if (!app.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限访问该应用");
        }
        String deployKey = app.getDeployKey();
        if (StrUtil.isBlank(deployKey)) {
            deployKey = RandomUtil.randomString(6);
        }
        //获取代码生成类型及，源路径
        String codeGenTypeStr = app.getCodeGenType();
        String sourceName = codeGenTypeStr + "_" + appId;
        String sourcePath = AppConstant.CODE_GEN_PATH + File.separator + sourceName;

        //检验代码文件是否存在
        if (!FileUtil.exist(sourcePath)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "代码不存在");
        }

        //若是Vue项目需要构建部署
        GenCodeTypeEnum codeGenTypeEnum = GenCodeTypeEnum.getEnumByValue(codeGenTypeStr);
        if (codeGenTypeEnum == GenCodeTypeEnum.VUE_PROJECT) {
            boolean isSuccess = vueProjectBuilder.buildVueProject(sourcePath);
            ThrowUtils.throwIf(!isSuccess, ErrorCode.SYSTEM_ERROR, "Vue 项目构建失败，请重试");
            // 检查 dist 目录是否存在
            File distDir = new File(sourcePath, "dist");
            ThrowUtils.throwIf(!distDir.exists(), ErrorCode.SYSTEM_ERROR, "Vue 项目构建完成但未生成 dist 目录");
            sourcePath = sourcePath + File.separator + "dist";
        }
        //将文件复制到部署目录下
        try {
            FileUtil.copy(sourcePath, AppConstant.CODE_DEPLOY_PATH + File.separator + deployKey, true);
        } catch (IORuntimeException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "部署失败:" + e.getMessage());
        }

        //更新部署时间
        App updateApp = new App();
        updateApp.setId(appId);
        updateApp.setDeployedTime(LocalDateTime.now());
        updateApp.setDeployKey(deployKey);
        boolean result = this.updateById(updateApp);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 构建应用访问 URL
        String appDeployUrl = String.format("%s/%s/", AppConstant.CODE_DEPLOY_DOMAIN, deployKey);
        // 11. 异步生成截图并更新应用封面
        generateAppScreenshotAsync(appId, appDeployUrl);
        //返回URL
        return appDeployUrl;
    }

    /**
     * 删除应用时删除关联的对话历史记录
     *
     * @param id
     * @return
     */
    @Override
    public boolean removeById(Serializable id) {
        if (id == null) {
            return false;
        }
        // 转换为 Long 类型
        Long appId = Long.valueOf(id.toString());
        if (appId <= 0) {
            return false;
        }
        // 先删除关联的对话历史
        try {
            chatHistoryService.deleteByAppId(appId);
        } catch (Exception e) {
            // 记录日志但不阻止应用删除
            log.error("删除应用关联对话历史失败: {}", e.getMessage());
        }
        // 删除应用
        return super.removeById(id);
    }

    /**
     * 异步生成应用封面
     *
     * @param appId
     * @param appDeployUrl
     */
    private void generateAppScreenshotAsync(Long appId, String appDeployUrl) {
        // 异步生成截图并更新应用封面
        Thread.startVirtualThread(() -> {
            try {
                String screenshotUrl = screenshotService.uploadScreenshot(appDeployUrl);
                App updateApp = new App();
                updateApp.setId(appId);
                updateApp.setCover(screenshotUrl);
                boolean updateResult = this.updateById(updateApp);
                ThrowUtils.throwIf(!updateResult, ErrorCode.OPERATION_ERROR, "更新应用封面失败");
            } catch (Exception e) {
            }
        });
    }
}





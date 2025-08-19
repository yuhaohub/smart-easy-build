package com.yuhao.smarteasybuild.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yuhao.smarteasybuild.model.dto.app.AppQueryRequest;
import com.yuhao.smarteasybuild.model.entity.App;
import com.yuhao.smarteasybuild.model.entity.User;
import com.yuhao.smarteasybuild.model.vo.AppVO;
import reactor.core.publisher.Flux;

import java.util.List;

/**
* @author hyh
* @description 针对表【app(应用表)】的数据库操作Service
* @createDate 2025-08-17 21:43:57
*/
public interface AppService extends IService<App> {

     AppVO getAppVO(App app);

     /**
      *  封装查询条件
      * @param appQueryRequest
      * @return
      */
     QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest);

     /**
      * 分页获取应用类别
      * @param appList
      * @return
      */
     List<AppVO> getAppVOList(List<App> appList);

     /**
      * 应用生成
      * @param appId
      * @param message
      * @param loginUser
      * @return
      */
     Flux<String> chatToGenCode(Long appId, String message, User loginUser);

     /**
      * 应用部署
      * @param appId
      * @param loginUser
      * @return
      */
     String deployApp(Long appId, User loginUser);
}

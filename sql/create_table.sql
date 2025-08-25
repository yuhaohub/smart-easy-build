CREATE DATABASE IF NOT EXISTS seb;
USE seb;

-- 用户表
CREATE TABLE IF NOT EXISTS user(
    id  bigint auto_increment  comment '用户id' primary key ,
    userAccount varchar(256)   not null  comment '帐号',
    userPassword varchar(256)  not null  comment '密码',
    userName varchar(256) null comment '用户昵称',
    userAvatar varchar(512)  null comment '用户头像',
    userProfile varchar(512) null comment '用户简介',
    userRole varchar(256) default 'user' not null comment '用户角色',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    editTime datetime default CURRENT_TIMESTAMP not null comment '用户编辑时间',
    updateTime datetime default CURRENT_TIMESTAMP not null comment '更新时间',
    isDelete tinyint default 0 not null comment '逻辑删除：0-1',
    UNIQUE key uk_userAccount(userAccount),
    INDEX idx_userName(userName)
)comment '用户表' collate = utf8mb4_unicode_ci;

-- 应用表
create table app
(
    id           bigint auto_increment comment 'id' primary key,
    appName      varchar(256)                       null comment '应用名称',
    cover        varchar(512)                       null comment '应用封面',
    initPrompt   text                               null comment '应用初始化的 prompt',
    codeGenType  varchar(64)                        null comment '代码生成类型（枚举）',
    deployKey    varchar(64)                        null comment '部署标识',
    deployedTime datetime                           null comment '部署时间',
    priority     int      default 0                 not null comment '优先级',
    userId       bigint                             not null comment '创建用户id',
    editTime     datetime default CURRENT_TIMESTAMP not null comment '编辑时间',
    createTime   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint  default 0                 not null comment '是否删除',
    UNIQUE KEY uk_deployKey (deployKey),
    INDEX idx_appName (appName),
    INDEX idx_userId (userId)
) comment '应用表' collate = utf8mb4_unicode_ci;
-- 对话历史表
create table chat_history
(
    id          bigint auto_increment comment 'id' primary key,
    message     text                               not null comment '消息',
    messageType varchar(32)                        not null comment 'user/ai',
    appId       bigint                             not null comment '应用id',
    userId      bigint                             not null comment '创建用户id',
    createTime  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint  default 0                 not null comment '是否删除',
    INDEX idx_appId (appId),                       -- 提升基于应用的查询性能
    INDEX idx_createTime (createTime),             -- 提升基于时间的查询性能
    INDEX idx_appId_createTime (appId, createTime) -- 游标查询核心索引
) comment '对话历史' collate = utf8mb4_unicode_ci;

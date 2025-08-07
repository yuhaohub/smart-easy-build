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
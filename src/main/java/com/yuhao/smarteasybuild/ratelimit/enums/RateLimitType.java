package com.yuhao.smarteasybuild.ratelimit.enums;

/**
 * 限流类型枚举
 */
public enum RateLimitType {
    /**
     * 接口级别限流
     */
    API,

    /**
     * 用户级别限流
     */
    USER,

    /**
     * IP级别限流
     */
    IP
}

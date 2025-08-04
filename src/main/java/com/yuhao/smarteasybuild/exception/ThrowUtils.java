package com.yuhao.smarteasybuild.exception;


/**
 * 异常工具类
 */
public class ThrowUtils {
    public static void throwIf(boolean condition, RuntimeException e) {
        if (condition) {
            throw e;
        }
    }
    public static void throwIf(boolean condition,ErrorCode errorCode) {
        if (condition) {
            throwIf(condition, new BusinessException(errorCode));
        }
    }
    public static void throwIf(boolean condition, ErrorCode errorCode,String message) {
        if (condition) {
            throwIf(condition, new BusinessException(errorCode,message));

        }
    }
}

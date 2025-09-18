package com.yuhao.smarteasybuild.monitor;


public class MonitorContextHolder {

    private static final ThreadLocal<MonitorContext> MONITOR_CONTEXT_HOLDER = new ThreadLocal<>();


    /**
     * 设置监控上下文
     */
    public static void setContext(MonitorContext context) {
        MONITOR_CONTEXT_HOLDER.set(context);
    }

    /**
     * 获取当前监控上下文
     */
    public static MonitorContext getContext() {
        return MONITOR_CONTEXT_HOLDER.get();
    }

    /**
     * 清除监控上下文
     */
    public static void clearContext() {
        MONITOR_CONTEXT_HOLDER.remove();
    }
}

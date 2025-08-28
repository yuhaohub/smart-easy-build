package com.yuhao.smarteasybuild.constant;

public interface AppConstant {

    /**
     * 精选应用的优先级
     */
    Integer GOOD_APP_PRIORITY = 99;

    /**
     * 默认应用优先级
     */
    Integer DEFAULT_APP_PRIORITY = 0;

    /**
     * 代码生成路径
     */
    String CODE_GEN_PATH = System.getProperty("user.dir") +"/tmp/code_output";


    /**
     * 代码部署路径
     */
    String CODE_DEPLOY_PATH = System.getProperty("user.dir") +"/tmp/code_deploy";

    /**
     * 代码部署域名
     */
    String CODE_DEPLOY_DOMAIN = "http://localhost";
}

package com.yuhao.smarteasybuild.model.enums;

import cn.hutool.core.util.ObjectUtil;

public enum GenCodeTypeEnum {

    HTML("原生 HTML 模式", "html"),
    HCJ("HCJ 模式", "hcj");

    private String name;
    private String value;

    GenCodeTypeEnum(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public static GenCodeTypeEnum getEnumByValue(String value){
        if (ObjectUtil.isEmpty(value)) {
            return  null;
        }
        for(GenCodeTypeEnum genCodeTypeEnum : GenCodeTypeEnum.values()){
            if (genCodeTypeEnum.value.equals(value)) {
                return genCodeTypeEnum;
            }
        }
        return  null;
    }
    public String getName() {
        return name;
    }
    public String getValue() {
        return value;
    }
}

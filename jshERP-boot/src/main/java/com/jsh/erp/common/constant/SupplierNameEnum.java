/**
 * Hellobike.com Inc.
 * Copyright (c) 2016-2022 All Rights Reserved.
 */
package com.jsh.erp.common.constant;

/**
 * @author chenshuai
 * @date 2022/9/19 16:55
 */
public enum SupplierNameEnum implements CommonConstant{
    /**
     * 供应商id,名
     */
    CAIYU(1,"彩云铁钎厂"),
    ZHENGXUAN(2,"正选钢芯厂")
    ;

    private final int code;
    private final String value;

    SupplierNameEnum(int code,String value){
        this.code = code;
        this.value = value;
    }
    @Override
    public int getCode() {
        return 0;
    }

    @Override
    public String getValue() {
        return null;
    }
}
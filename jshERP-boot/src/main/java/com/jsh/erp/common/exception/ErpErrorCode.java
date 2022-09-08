/**
 * Hellobike.com Inc.
 * Copyright (c) 2016-2022 All Rights Reserved.
 */
package com.jsh.erp.common.exception;

import lombok.Getter;

/**
 * @author chenshuai
 * @date 2022/9/5 15:25
 */
@Getter
public enum ErpErrorCode implements ErrorCodeConstants{
    ;
    private final int code;
    private final String msg;

    ErpErrorCode(int code,String msg){
        this.code = code;
        this.msg = msg;
    }
}
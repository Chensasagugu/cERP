/**
 * Hellobike.com Inc.
 * Copyright (c) 2016-2022 All Rights Reserved.
 */
package com.jsh.erp.common.constant;

/**
 * 供应商种类
 *
 * @author chenshuai
 * @date 2022/9/5 16:07
 */
public enum SupplierType implements CommonConstant{
  /**
   * 供应商
   */
  SUPPLIER(1,"供应商"),
  /**
   * 客户
   */
  CUSTOMER(2, "客户")
  ;

  private final int code;
  private final String value;

  SupplierType(int code, String value) {
    this.code = code;
    this.value = value;
  }

  @Override
  public int getCode() {
    return code;
  }

  @Override
  public String getValue() {
    return value;
  }

}




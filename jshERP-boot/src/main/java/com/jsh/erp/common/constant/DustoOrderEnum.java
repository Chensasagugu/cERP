/**
 * Hellobike.com Inc.
 * Copyright (c) 2016-2022 All Rights Reserved.
 */
package com.jsh.erp.common.constant;

/**
 * 大东订单excel表格常量
 *
 * @author chenshuai
 * @date 2022/9/8 09:53
 */
public enum DustoOrderEnum {
  /**
   * 表头信息（索引，列名）
   */
  COLUMN_DATE(0,"凭证日期"),
  COLUMN_SEQUENCE(1,"采购凭证"),
  COLUMN_NAME(4,"短文本"),
  COLUMN_STANDARDS(5,"规格描述"),
  COLUMN_PLAN_NUMBER(6,"已计划数量"),
  COLUMN_UNIT(7,"计量单位"),
  COLUMN_REMAIN_NUMBER(9,"剩余数量"),
  COLUMN_MODEL(12,"参考货号"),
  COLUMN_CUSTOMER(15,"地址")
  ;
  /**
   * 列索引
   */
  private final int index;
  /**
   * 列名
   */
  private final String value;

  DustoOrderEnum(int index, String value) {
    this.index = index;
    this.value = value;
  }

  public int getIndex() {
    return this.index;
  }

  public String getValue() {
    return this.value;
  }
}




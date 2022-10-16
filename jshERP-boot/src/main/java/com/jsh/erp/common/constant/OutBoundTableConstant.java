/**
 * Hellobike.com Inc.
 * Copyright (c) 2016-2022 All Rights Reserved.
 */
package com.jsh.erp.common.constant;

/**
 * @author chenshuai
 * @date 2022/10/8 17:01
 */
public enum OutBoundTableConstant{
  /**
   * 出库图片中的表格单元格左上角列索引
   */
  YUANYI_ZHUSU_TAG(0,"有问题"),
  YUANYI_ZHUSU_START_ROW(2,"开始的行号"),
  YUANYI_ZHUSU_TIME(0,"送货时间"),
  YUANYI_ZHUSU_SPU(4,"型体"),
  YUANYI_ZHUSU_34(5,"34#"),
  YUANYI_ZHUSU_35(6,"35#"),
  YUANYI_ZHUSU_36(7,"36#"),
  YUANYI_ZHUSU_37(8,"37#"),
  YUANYI_ZHUSU_38(9,"38#"),
  YUANYI_ZHUSU_39(10,"39#"),
  YUANYI_ZHUSU_40(11,"40#"),
  YUANYI_ZHUSU_41(12,"41#"),
  YUANYI_ZHUSU_42(13,"42#"),

  YUANYI_TRADITION_TAG(0,"送货"),
  YUANYI_TRADITION_START_ROW(1,"开始的行号"),
  YUANYI_TRADITION_TIME(0,"送货日期"),
  YUANYI_TRADITION_SPU(4,"型号"),
  YUANYI_TRADITION_34(5,"34#"),
  YUANYI_TRADITION_35(6,"35#"),
  YUANYI_TRADITION_36(7,"36#"),
  YUANYI_TRADITION_37(8,"37#"),
  YUANYI_TRADITION_38(9,"38#"),
  YUANYI_TRADITION_39(10,"39#"),
  YUANYI_TRADITION_40(11,"40#"),

  FENGYI_START_ROW(1,"开始的行号"),
  FENGYI_SPU(0,"型号"),
  FENGYI_34(2,"34#"),
  FENGYI_35(3,"35#"),
  FENGYI_36(4,"36#"),
  FENGYI_37(5,"37#"),
  FENGYI_38(6,"38#"),
  FENGYI_39(7,"39#"),
  FENGYI_40(8,"40#"),
  FENGYI_41(9,"41#"),
  FENGYI_42(10,"42#"),
  FENGYI_43(11,"43#"),
  FENGYI_44(12,"44#")
  ;
  /**
   * 列索引
   */
  private final int index;
  /**
   * 列名
   */
  private final String value;

  OutBoundTableConstant(int index, String value) {
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




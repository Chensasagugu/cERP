/**
 * Hellobike.com Inc.
 * Copyright (c) 2016-2022 All Rights Reserved.
 */
package com.jsh.erp.domin.vo;

import lombok.Data;

import java.time.LocalDate;

/**
 * 大东订单Vo
 *
 * @author chenshuai
 * @date 2022/9/7 17:45
 */
@Data
public class DustoOrderVo {
    /**
     * 订单日期
     */
    LocalDate date;

    /**
     * 采购单号
     */
    String sequence;

    /**
     * 名称
     */
    String name;

    /**
     * 货号
     */
    String model;
    /**
     * 规格
     */
    String standards;

    /**
     * 计划数量
     */
    Integer planNumber;

    /**
     * 剩余数量
     */
    Integer remainNumber;

    /**
     * 下单厂商
     */
    String customer;
}
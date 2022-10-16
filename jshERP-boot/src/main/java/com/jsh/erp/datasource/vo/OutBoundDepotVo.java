/**
 * Hellobike.com Inc.
 * Copyright (c) 2016-2022 All Rights Reserved.
 */
package com.jsh.erp.datasource.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 出库单vo
 *
 * @author chenshuai
 * @date 2022/10/10 11:09
 */
@Data
public class OutBoundDepotVo {

    /**
     * id
     */
    private Long id;

    /**
     * 类型
     */
    private String type;

    /**
     * 子类型
     */
    private String subType;

    /**
     * 单号
     */
    private List<String> numbers;

    /**
     * 货号
     */
    private List<String> models;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date operTime;

    /**
     * 供应商
     */
    private String organ;

    /**
     * 合计金额
     */
    private BigDecimal totalPrice;

    /**
     * 状态（状态，0未审核、1已审核、2完成销售、3部分销售）
     */
    private String status;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 具体单据项
     */
    List<DepotItemVo> items;
}
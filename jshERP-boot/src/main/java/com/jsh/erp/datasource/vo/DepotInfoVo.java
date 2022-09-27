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
 * 单据全部信息
 *
 * @author chenshuai
 * @date 2022/9/8 11:24
 */
@Data
public class DepotInfoVo {

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
     * 初始单号
     */
    private String defaultNumber;

    /**
     * 单号
     */
    private String number;

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
     * 付款类型（现金，记帐）
     */
    private String payType;

    /**
     * 备注
     */
    private String remark;

    /**
     * 定金
     */
    private BigDecimal deposit;

    /**
     * 状态（状态，0未审核、1已审核、2完成销售、3部分销售）
     */
    private String status;

    /**
     * 关联订单号
     */
    private String linkNumber;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 具体单据项
     */
    List<DepotItemVo> items;
}
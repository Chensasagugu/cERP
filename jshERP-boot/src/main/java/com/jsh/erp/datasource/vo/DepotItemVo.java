/**
 * Hellobike.com Inc.
 * Copyright (c) 2016-2022 All Rights Reserved.
 */
package com.jsh.erp.datasource.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 单据项
 *
 * @author chenshuai
 * @date 2022/9/8 13:55
 */
@Data
public class DepotItemVo {
    /**
     * id
     */
    private Long id;

    /**
     * 单据头id
     */
    private Long headerId;

    /**
     * 产品id
     */
    private Long materialId;

    /**
     * skuId
     */
    private Long materialExtendId;

    /**
     * 产品名
     */
    private String materialName;

    /**
     * 产品货号
     */
    private String materialModel;

    /**
     * 规格
     */
    private String standards;
    /**
     * 产品单位
     */
    private String materialUnit;

    /**
     * 剩余数量(operNumber)
     */
    private Integer remainNumber;

    /**
     * 总数(basicNumber)
     */
    private Integer totalNumber;

    /**
     * 单价
     */
    private BigDecimal unitPrice;

    /**
     * 总价
     */
    private BigDecimal allPrice;

    /**
     * 备注
     */
    private String remark;

    /**
     * 租户id
     */
    private Long tenantId;
}
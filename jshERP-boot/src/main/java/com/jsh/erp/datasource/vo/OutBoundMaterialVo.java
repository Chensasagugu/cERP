/**
 * Hellobike.com Inc.
 * Copyright (c) 2016-2022 All Rights Reserved.
 */
package com.jsh.erp.datasource.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * 出库单产品vo
 *
 * @author chenshuai
 * @date 2022/10/8 16:45
 */
@Data
public class OutBoundMaterialVo {

    /**
     * 送货时间
     */
    private LocalDate time;

    /**
     * spuId
     */
    private Long materialId;

    /**
     * skuId
     */
    private Long skuId;

    /**
     * 产品名
     */
    private String name;

    /**
     * 货号
     */
    private String model;

    /**
     * 规格
     */
    private String sku;

    /**
     * 单价
     */
    private BigDecimal price;

    /**
     * 数量
     */
    private Integer number;

    /**
     * 备注
     */
    private String remark;


}
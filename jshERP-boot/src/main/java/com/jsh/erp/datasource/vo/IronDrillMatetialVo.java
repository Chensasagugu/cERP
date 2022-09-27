/**
 * Hellobike.com Inc.
 * Copyright (c) 2016-2022 All Rights Reserved.
 */
package com.jsh.erp.datasource.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 铁钎的产品Vo
 *
 * @author chenshuai
 * @date 2022/9/23 16:45
 */
@Data
public class IronDrillMatetialVo {

    /**
     * id
     */
    private String id;

    /**
     * 产品名
     */
    private String name;

    /**
     * 货号
     */
    private String model;

    /**
     * 制造商
     */
    private String mfrs;

    /**
     * 单位
     */
    private String unit;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否可用
     */
    private Boolean enabled;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 删除标识
     */
    private String deleteFlag;

    /**
     * sku
     */
    private String sku;

    /**
     * 尺寸（宽*厚*长）
     */
    private String size;

    /**
     * 单价
     */
    private BigDecimal price;

    /**
     * 创建日期
     */
    private Date createTime;

    /**
     * 更新日期
     */
    private Long updateTime;
}
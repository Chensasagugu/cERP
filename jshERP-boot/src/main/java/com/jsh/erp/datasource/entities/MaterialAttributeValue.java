package com.jsh.erp.datasource.entities;

import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 产品属性值表
 * </p>
 *
 * @author chenASa
 * @since 2022-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class MaterialAttributeValue implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 产品sku的id
     */
    private Long skuId;

    /**
     * 属性id
     */
    private Long attributeId;

    /**
     * 属性值
     */
    private String value;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 删除标记，0未删除，1删除
     */
    private String deleteFlag;


}

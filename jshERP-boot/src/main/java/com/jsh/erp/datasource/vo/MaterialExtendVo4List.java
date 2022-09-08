package com.jsh.erp.datasource.vo;

import com.jsh.erp.datasource.entities.MaterialExtend;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MaterialExtendVo4List extends MaterialExtend {

    private String supplier;

    private String originPlace;

    private String unit;

    private String brandName;

    private BigDecimal guaranteePeriod;

    private BigDecimal memberDecimal;

}

/**
 * Hellobike.com Inc.
 * Copyright (c) 2016-2022 All Rights Reserved.
 */
package com.jsh.erp.factory;

import com.jsh.erp.common.constant.DustoOrderEnum;
import com.jsh.erp.constants.BusinessConstants;
import com.jsh.erp.datasource.vo.DepotInfoVo;
import com.jsh.erp.datasource.vo.DepotItemVo;
import io.swagger.models.auth.In;
import jxl.Cell;
import jxl.Sheet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 单据转换器
 *
 * @author chenshuai
 * @date 2022/9/8 11:19
 */
@Component
public class DepotConvert {

    /**
     * 将大东订单工作表变为单据Vo
     *
     * @param sheet
     * @return
     */
    public List<DepotInfoVo> convertToDepotInfo(Sheet sheet) {
        HashMap<String, DepotInfoVo> map = new HashMap<>();
        for (int i = 1; i < sheet.getRows(); i++) {
            Cell[] row = sheet.getRow(i);
            if (row.length <= DustoOrderEnum.COLUMN_CUSTOMER.getIndex()) {
                continue;
            }
            String sequence = row[DustoOrderEnum.COLUMN_SEQUENCE.getIndex()].getContents();
            //创建单据项
            DepotItemVo depotItem = new DepotItemVo();
            depotItem.setMaterialName(row[DustoOrderEnum.COLUMN_NAME.getIndex()].getContents());
            depotItem.setMaterialModel(row[DustoOrderEnum.COLUMN_MODEL.getIndex()].getContents());
            depotItem.setMaterialUnit(row[DustoOrderEnum.COLUMN_UNIT.getIndex()].getContents());
            depotItem.setRemainNumber(Integer.valueOf(row[DustoOrderEnum.COLUMN_REMAIN_NUMBER.getIndex()].getContents()));
            depotItem.setTotalNumber(Integer.valueOf(row[DustoOrderEnum.COLUMN_PLAN_NUMBER.getIndex()].getContents()));
            depotItem.setStandards(row[DustoOrderEnum.COLUMN_STANDARDS.getIndex()].getContents());
            //TODO 设置租户id

            //创建或获得单据头
            DepotInfoVo depotInfo;
            if (!map.containsKey(sequence)) {
                //还没有这个单号则创建单据头
                depotInfo = new DepotInfoVo();
                depotInfo.setType(BusinessConstants.DEPOTHEAD_TYPE_OTHER);
                depotInfo.setSubType(BusinessConstants.SUB_TYPE_SALES_ORDER);
                depotInfo.setNumber(sequence);
                depotInfo.setDefaultNumber(sequence);
                depotInfo.setOrgan(row[DustoOrderEnum.COLUMN_CUSTOMER.getIndex()].getContents());
                //TODO 计算合计金额
                depotInfo.setPayType(BusinessConstants.PAY_TYPE_BOOKKEEPING);
                depotInfo.setRemark(BusinessConstants.DUSTO_REMARK);
                depotInfo.setStatus(BusinessConstants.BILLS_STATUS_AUDIT);
                //TODO 设置租户id

                depotInfo.setItems(new ArrayList<>());
                map.put(sequence, depotInfo);
            } else {
                //已经有这个单号了
                depotInfo = map.get(sequence);
            }
            List<DepotItemVo> list = depotInfo.getItems();
            list.add(depotItem);
            depotInfo.setItems(list);
        }
        List<DepotInfoVo> depotInfoVos = new ArrayList<>(map.values());
        combineDepotItem(depotInfoVos);
        //合并同类数量
        return depotInfoVos;
    }

    /**
     * 将订单中相同类型的产品数量合并
     * @param depotInfoVos
     */
    private void combineDepotItem(List<DepotInfoVo> depotInfoVos) {
        for (DepotInfoVo depotInfo:depotInfoVos) {
            HashMap<Material, DepotItemVo> map = new HashMap<>();
            for(DepotItemVo item: depotInfo.getItems()){
                Material material = new Material(item.getMaterialName(), item.getMaterialModel(),item.getStandards());
                if(map.containsKey(material)){
                    DepotItemVo depotItemVo = map.get(material);
                    depotItemVo.setTotalNumber(depotItemVo.getTotalNumber()+item.getTotalNumber());
                    depotItemVo.setRemainNumber(depotItemVo.getRemainNumber()+item.getRemainNumber());
                    map.put(material,depotItemVo);
                }else{
                    map.put(material,item);
                }
            }
            List<DepotItemVo> res = new ArrayList<>(map.values());
            depotInfo.setItems(res);
        }
    }

    @Data
    @EqualsAndHashCode
    @AllArgsConstructor
    static class Material {
        private String name;
        private String model;
        private String standards;
    }
}
/**
 * Hellobike.com Inc.
 * Copyright (c) 2016-2022 All Rights Reserved.
 */
package com.jsh.erp.domin.service.impl;

import com.jsh.erp.constants.BusinessConstants;
import com.jsh.erp.datasource.vo.DepotInfoVo;
import com.jsh.erp.datasource.vo.OutBoundDepotVo;
import com.jsh.erp.datasource.vo.OutBoundMaterialVo;
import com.jsh.erp.domin.service.LoadDustoService;
import com.jsh.erp.factory.DepotConvert;
import com.jsh.erp.factory.MaterialConvert;
import com.jsh.erp.service.depotHead.DepotHeadService;
import com.jsh.erp.thirdparty.tableocr.TableOCRUtils;
import com.tencentcloudapi.ocr.v20181119.models.TableDetectInfo;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author chenshuai
 * @date 2022/9/7 17:26
 */
@Slf4j
@Service("LoadDustoService")
public class LoadDustoServiceImpl implements LoadDustoService {

    @Resource
    DepotConvert depotConvert;
    @Resource
    DepotHeadService depotHeadService;
    @Resource
    MaterialConvert materialConvert;



    @Override
    public void enterOrder(File file) {
        //读取excel文件
        try {
            Workbook workbook = Workbook.getWorkbook(file);
            Sheet sheet = workbook.getSheet(0);
            List<DepotInfoVo> dustoOrderVos = depotConvert.convertToDepotInfo(sheet);
            //depotHeadService.saveOrder(dustoOrderVos);
            depotHeadService.saveOrder(dustoOrderVos);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void generateOutboundOrder(File image) {
        TableDetectInfo tableDetections = TableOCRUtils.testTableOCR().getTableDetections()[0];
        List<OutBoundMaterialVo> outBoundMaterialVos = materialConvert.convertTableDetectionInfoToMaterialVo(tableDetections);
        List<List<OutBoundMaterialVo>> classification = classification(outBoundMaterialVos);
        //创建出库单
        for(List<OutBoundMaterialVo> outBoundItems:classification){

        }
    }

    /**
     * 创建出库单
     * @param outBoundItems
     * @return
     */
    private OutBoundDepotVo createOutBoundDepotVo(List<OutBoundMaterialVo> outBoundItems){
        OutBoundDepotVo outBoundDepotVo = new OutBoundDepotVo();
        List<String> numbers = new ArrayList<>();
        List<String> models = new ArrayList<>();
        outBoundDepotVo.setType(BusinessConstants.DEPOTHEAD_TYPE_OUT);
        outBoundDepotVo.setSubType(BusinessConstants.SUB_TYPE_SALES);
        return null;

    }

    /**
     * 分类，将同一型号不同货号的铁钎合为一类
     *
     * @param list
     * @return
     */
    private List<List<OutBoundMaterialVo>> classification(List<OutBoundMaterialVo> list){
        Map<String,List<OutBoundMaterialVo>> map = new HashMap<>();
        for(OutBoundMaterialVo vo:list){
            String name = vo.getName();
            List<OutBoundMaterialVo> outBound = map.getOrDefault(name, new ArrayList<>());
            outBound.add(vo);
            map.put(name,outBound);
        }
        List<List<OutBoundMaterialVo>> collect = map.entrySet().stream().map(ent -> ent.getValue())
                .collect(Collectors.toList());
        return collect;
    }
}
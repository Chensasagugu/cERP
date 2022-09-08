/**
 * Hellobike.com Inc.
 * Copyright (c) 2016-2022 All Rights Reserved.
 */
package com.jsh.erp.domin.service.impl;

import com.jsh.erp.common.constant.DustoOrderEnum;
import com.jsh.erp.datasource.vo.DepotInfoVo;
import com.jsh.erp.domin.service.LoadDustoService;
import com.jsh.erp.domin.vo.DustoOrderVo;
import com.jsh.erp.factory.DepotConvert;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.read.biff.RowRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chenshuai
 * @date 2022/9/7 17:26
 */
@Slf4j
@Service("LoadDustoService")
public class LoadDustoServiceImpl implements LoadDustoService {

    @Resource
    DepotConvert depotConvert;

    @Override
    public void enterOrder(File file) {
        //读取excel文件
        try {
            Workbook workbook = Workbook.getWorkbook(file);
            Sheet sheet = workbook.getSheet(0);
            List<DepotInfoVo> dustoOrderVos = depotConvert.convertToDepotInfo(sheet);
            System.out.println(dustoOrderVos);
            //Cell[] row = sheet.getRow(116);
            //log.info(String.valueOf(row.length));
            //log.info(row[DustoOrderEnum.COLUMN_MODEL.getIndex()].getContents());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        }
    }
}
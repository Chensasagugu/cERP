/**
 * Hellobike.com Inc.
 * Copyright (c) 2016-2022 All Rights Reserved.
 */
package com.jsh.erp.factory;

import com.jsh.erp.datasource.vo.OutBoundMaterialVo;
import com.tencentcloudapi.ocr.v20181119.models.TableCell;
import com.tencentcloudapi.ocr.v20181119.models.TableDetectInfo;
import lombok.Data;
import org.mapstruct.Mapper;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @author chenshuai
 * @date 2022/10/8 16:50
 */
@Mapper(componentModel = "spring")
public interface MaterialConvert {

    default List<OutBoundMaterialVo> convertTableDetectionInfoToMaterialVo(TableDetectInfo info) {
        List<OutBoundMaterialVo> resList = new ArrayList<>();
        TableCell[] cells = info.getCells();
        OutBoundTable table = new OutBoundTable();
        String nowMaterial = "";
        String nowTime = "";
        boolean hasTimeIndex = false;
        for (int i=0;i<cells.length;i++){
            String text = cells[i].getText();
            if(text.contains("送货有问题")||StringUtils.isEmpty(text)){
                continue;
            }
            if(text.contains("型")){
                table.setMaterialColIndex(cells[i].getColTl());
            }else if(text.contains("送货")&&!hasTimeIndex){
                //送货时间
                table.setTimeColIndex(cells[i].getColTl());
                hasTimeIndex = true;
            }else if(text.contains("34#")){
                table.setSku34Index(cells[i].getColTl());
            }else if(text.contains("35#")){
                table.setSku35Index(cells[i].getColTl());
            }else if(text.contains("36#")){
                table.setSku36Index(cells[i].getColTl());
            }else if(text.contains("37#")){
                table.setSku37Index(cells[i].getColTl());
            }else if(text.contains("38#")){
                table.setSku38Index(cells[i].getColTl());
            }else if(text.contains("39#")){
                table.setSku39Index(cells[i].getColTl());
            }else if(text.contains("40#")){
                table.setSku40Index(cells[i].getColTl());
            }else if(text.contains("41#")){
                table.setSku41Index(cells[i].getColTl());
            }else if(text.contains("42#")){
                table.setSku42Index(cells[i].getColTl());
            }else if(text.contains("43#")){
                table.setSku43Index(cells[i].getColTl());
            }else if(text.contains("44#")){
                table.setSku44Index(cells[i].getColTl());
            }else{
                Long colTl = cells[i].getColTl();
                if(colTl.equals(table.getMaterialColIndex())){
                    nowMaterial = text;
                }
                if(colTl.equals(table.getTimeColIndex())){
                    nowTime = text;
                }
                if(!StringUtils.isEmpty(nowMaterial)&&colTl.equals(table.getSku34Index())&&!StringUtils.isEmpty(text)){
                    OutBoundMaterialVo vo = createOutBoundMaterialVo(nowTime,nowMaterial,"34",Integer.valueOf(text));
                    resList.add(vo);
                }
                if(!StringUtils.isEmpty(nowMaterial)&&colTl.equals(table.getSku35Index())&&!StringUtils.isEmpty(text)){
                    OutBoundMaterialVo vo = createOutBoundMaterialVo(nowTime,nowMaterial,"35",Integer.valueOf(text));
                    resList.add(vo);
                }
                if(!StringUtils.isEmpty(nowMaterial)&&colTl.equals(table.getSku36Index())&&!StringUtils.isEmpty(text)){
                    OutBoundMaterialVo vo = createOutBoundMaterialVo(nowTime,nowMaterial,"36",Integer.valueOf(text));
                    resList.add(vo);
                }
                if(!StringUtils.isEmpty(nowMaterial)&&colTl.equals(table.getSku37Index())&&!StringUtils.isEmpty(text)){
                    OutBoundMaterialVo vo = createOutBoundMaterialVo(nowTime,nowMaterial,"37",Integer.valueOf(text));
                    resList.add(vo);
                }
                if(!StringUtils.isEmpty(nowMaterial)&&colTl.equals(table.getSku38Index())&&!StringUtils.isEmpty(text)){
                    OutBoundMaterialVo vo = createOutBoundMaterialVo(nowTime,nowMaterial,"38",Integer.valueOf(text));
                    resList.add(vo);
                }
                if(!StringUtils.isEmpty(nowMaterial)&&colTl.equals(table.getSku39Index())&&!StringUtils.isEmpty(text)){
                    OutBoundMaterialVo vo = createOutBoundMaterialVo(nowTime,nowMaterial,"39",Integer.valueOf(text));
                    resList.add(vo);
                }
                if(!StringUtils.isEmpty(nowMaterial)&&colTl.equals(table.getSku40Index())&&!StringUtils.isEmpty(text)){
                    OutBoundMaterialVo vo = createOutBoundMaterialVo(nowTime,nowMaterial,"40",Integer.valueOf(text));
                    resList.add(vo);
                }
                if(!StringUtils.isEmpty(nowMaterial)&&colTl.equals(table.getSku41Index())&&!StringUtils.isEmpty(text)){
                    OutBoundMaterialVo vo = createOutBoundMaterialVo(nowTime,nowMaterial,"41",Integer.valueOf(text));
                    resList.add(vo);
                }
                if(!StringUtils.isEmpty(nowMaterial)&&colTl.equals(table.getSku42Index())&&!StringUtils.isEmpty(text)){
                    OutBoundMaterialVo vo = createOutBoundMaterialVo(nowTime,nowMaterial,"42",Integer.valueOf(text));
                    resList.add(vo);
                }
                if(!StringUtils.isEmpty(nowMaterial)&&colTl.equals(table.getSku43Index())&&!StringUtils.isEmpty(text)){
                    OutBoundMaterialVo vo = createOutBoundMaterialVo(nowTime,nowMaterial,"43",Integer.valueOf(text));
                    resList.add(vo);
                }
                if(!StringUtils.isEmpty(nowMaterial)&&colTl.equals(table.getSku44Index())&&!StringUtils.isEmpty(text)){
                    OutBoundMaterialVo vo = createOutBoundMaterialVo(nowTime,nowMaterial,"44",Integer.valueOf(text));
                    resList.add(vo);
                }
            }
        }
        return resList;
    }
    default OutBoundMaterialVo createOutBoundMaterialVo(String nowTime, String nowMaterial, String sku, Integer num){
        OutBoundMaterialVo vo = new OutBoundMaterialVo();
        String[] split = nowMaterial.split("-");
        vo.setName(split[0]);
        vo.setModel(split[1]);
        vo.setSku(sku);
        vo.setNumber(num);
        vo.setTime(parseTime(nowTime));
        return vo;
    }
    default LocalDate parseTime(String time){
        int month = 0;
        int day = 0;
        int i=0;
        while(time.charAt(i)!='月'){
            char ch = time.charAt(i);
            if(ch>='0'&&ch<='9'){
                month = month*10+(ch-'0');
            }
            i++;
        }
        while(time.charAt(i)!='日'){
            char ch = time.charAt(i);
            if(ch>='0'&&ch<='9'){
                day = day*10+(ch-'0');
            }
            i++;
        }
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        LocalDate date = LocalDate.of(year,month,day);
        return date;
    }

    @Data
    class OutBoundTable{
        private Long timeColIndex;

        private Long materialColIndex;

        private Long sku34Index;

        private Long sku35Index;

        private Long sku36Index;

        private Long sku37Index;

        private Long sku38Index;

        private Long sku39Index;

        private Long sku40Index;

        private Long sku41Index;

        private Long sku42Index;

        private Long sku43Index;

        private Long sku44Index;
    }
}
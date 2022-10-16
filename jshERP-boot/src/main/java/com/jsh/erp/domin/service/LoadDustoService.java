/**
 * Hellobike.com Inc.
 * Copyright (c) 2016-2022 All Rights Reserved.
 */
package com.jsh.erp.domin.service;

import org.springframework.stereotype.Service;

import java.io.File;

/**
 * 加载大东excel文件的服务
 *
 * @author chenshuai
 * @date 2022/9/7 17:22
 */
public interface LoadDustoService {

    /**
     * 将大东订单录入数据库单据中
     * @param file
     */
    void enterOrder(File file);

    /**
     * 输入大东送货图片，生成对应出库单
     * @param image
     */
    void generateOutboundOrder(File image);


}
/**
 * Hellobike.com Inc.
 * Copyright (c) 2016-2022 All Rights Reserved.
 */
package com.jsh.erp.datasource.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 自动填充配置
 * @author chenshuai
 * @date 2022/9/20 14:08
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        this.setFieldValByName("createTime",new Date(),metaObject);
        this.setFieldValByName("operTime",new Date(),metaObject);
        metaObject.setValue("deleteFlag","0");
    }


    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("operTime",new Date(),metaObject);
    }
}
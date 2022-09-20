/**
 * MySQLCodeGenerator.java
 * Copyright 2021 HelloBike , all rights reserved.
 * HelloBike PROPRIETARY/CONFIDENTIAL, any form of usage is subject to approval.
 */

package com.hellobike.carbon.detective.infrastructure.util;


import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author yumianhui09923@hellobike.com
 * @date 2021/11/3
 */
public class MySQLCodeGenerator {
    private final static String PARENT_PACKAGE_NAME = "com.hellobike.carbon.detective.infrastructure.presistence";

    /**
     * <p>
     * 读取控制台内容
     * </p>
     */
    public static String scanner(String tip) {
        Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8.name());
        System.out.println("请输入" + tip + "：");
        if (scanner.hasNext()) {
            String ipt = scanner.next();
            if (StringUtils.isNotEmpty(ipt)) {
                return ipt;
            }
        }
        throw new MybatisPlusException("请输入正确的" + tip + "！");
    }

    public static void main(String[] args) {
        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        String projectPath = System.getProperty("user.dir");
        gc.setOutputDir(projectPath + "/carbon-detective-infrastructure/src/main/java");
        gc.setAuthor("mybatisplus generator");
        gc.setOpen(false);
        //实体属性 Swagger2 注解
        gc.setSwagger2(false);
        gc.setFileOverride(true);
        gc.setEntityName("%sPO");
        gc.setServiceName("%sRepository");
        gc.setServiceImplName("%sRepositoryImpl");
        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl("jdbc:mysql://10.69.18.215:33071/carbon_detective?useSSL=false");
        dsc.setDriverName("com.mysql.jdbc.Driver");
        dsc.setUsername("carbon_detective_user");
        dsc.setPassword("d@fT4s$D5Gt");

        // 自定义配置
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                // to do nothing
            }
        };

        // 自定义输出配置
        List<FileOutConfig> focList = new ArrayList<>();
        // 自定义配置会被优先输出
        focList.add(new FileOutConfig("/templates/mapper.xml.vm") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                // 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
                return projectPath + "/carbon-detective-infrastructure/src/main/resources/mybatis/mapper/" + tableInfo.getEntityName() + "Mapper" + StringPool.DOT_XML;
            }
        });
        cfg.setFileOutConfigList(focList);

        // 配置模板
        TemplateConfig templateConfig = new TemplateConfig();
        templateConfig.setXml(null);
        templateConfig.setController(null);

        // 策略配置
        StrategyConfig strategy = new StrategyConfig()
                .setEntityColumnConstant(true)
                .setEntityLombokModel(true)
                .setRestControllerStyle(false)
                .setSuperEntityColumns("id")
                .setInclude(scanner("表名，多个英文逗号分割").split(","))
                .setControllerMappingHyphenStyle(false)
//                .setEntitySerialVersionUID(false)
                .setEntityBooleanColumnRemoveIsPrefix(true)
                .setEntityTableFieldAnnotationEnable(true)
                //表名下划线对应驼峰
                .setNaming(NamingStrategy.underline_to_camel)
                //设定表前缀，加上该配置后，会在表上自动生成@TableName注解
                .setTablePrefix("t_")
                .setColumnNaming(NamingStrategy.underline_to_camel);

        PackageConfig packageConfig = new PackageConfig();
        packageConfig.setParent(PARENT_PACKAGE_NAME);
        packageConfig.setEntity("po");
        packageConfig.setMapper("mapper");
        packageConfig.setService("repository");
        packageConfig.setServiceImpl("repository.impl");
        new AutoGenerator().setGlobalConfig(gc)
                .setDataSource(dsc)
                .setCfg(cfg)
                .setStrategy(strategy)
                .setTemplate(templateConfig)
                .setTemplateEngine(new VelocityTemplateEngine())
                .setPackageInfo(packageConfig)
                .execute();
    }
}
package com.jsh.erp.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 字段描述
 *
 * @author chenkangkang
 * @date 2022/8/8 10:56 AM
 **/

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldDescription {

    String value() default "";
}

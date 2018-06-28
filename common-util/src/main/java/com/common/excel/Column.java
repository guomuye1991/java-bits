package com.common.excel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Gmy on 2017/6/2.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {

    /**
     * @return 列名
     */
    String value();

    /**
     * @return excel 单元格顺序
     */
    int ordered();

    /**
     * @return 日期格式化字符串
     */
    String format() default "";
}

package com.common.datetime;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateTimeUtil {

    /**
     * 根据格式解析字符串
     *
     * @param dateStr 日期字符串
     * @param format  格式
     * @return 解析后时间
     */
    public static LocalDateTime parse(String dateStr, String format) {
        if (StringUtils.isBlank(dateStr)) return null;
        return LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern(format));
    }

    /**
     * 将一个日期字符串转为另一种格式
     *
     * @param dateStr 字符串
     * @param sour    原格式
     * @param des     目标格式
     * @return 目标格式字符串
     */
    public static String convert(String dateStr, String sour, String des) {
        if (StringUtils.isBlank(dateStr)) return null;
        return LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern(sour)).format(DateTimeFormatter.ofPattern(des));
    }


    public static void main(String[] args) {
        System.out.println(LocalDateTime.of(2018,6,27,13,25).until(LocalDateTime.now(),ChronoUnit.MINUTES));
    }
}
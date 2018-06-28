package com.common.excel;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Gmy on 2017/6/2.
 */
public class ExcelUtil {

    /**
     * @apiNote 暂时不支持继承关系的类，暂时对excel每页的sheet数量做上线处理
     */
    public static <T> Workbook writeCountryListToFile( String fileName, String sheetName,
                                                      List<String> headers,
                                                       List<T> body,  Class<T> tClass, Integer sheetCount) throws Exception {
        Workbook workbook;
        if (fileName.endsWith("xlsx")) {
            workbook = new XSSFWorkbook();
        } else if (fileName.endsWith("xls")) {
            workbook = new HSSFWorkbook();
        } else {
            throw new Exception("invalid file name, should be xls or xlsx");
        }
        process(workbook, sheetName, headers, body, tClass, sheetCount);
        return workbook;
    }

    public static <T> void process(Workbook workbook, String sheetName, List<String> headers, List<T> body,
                                   Class tClass, Integer sheetCount) throws IllegalAccessException {
        sheetCount = sheetCount == null ? 1000000 : sheetCount;
        //根据反射获取注解中的列标题
        Field[] fields = tClass.getDeclaredFields();
        //过滤只有带有注解的fields，并保证数序
        List<Field> fieldsFilter = Arrays.stream(fields).filter(e -> e.getAnnotation(Column.class) != null)
                .sorted(Comparator.comparingInt(o -> o.getAnnotation(Column.class).ordered()))
                .collect(Collectors.toList());

        Sheet sheet = workbook.createSheet(sheetName);
        int rowIndex = processTitle(sheet, headers, fieldsFilter);
        //迭代数据
        Integer count = 1;
        Integer tab = 1;
        for (T rowData : body) {
            //代表进行到下一sheet
            if (count % sheetCount == 0) {
                sheet = workbook.createSheet(String.format("%s-%s", sheetName, tab++));
                processTitle(sheet, headers, fieldsFilter);
                rowIndex = processTitle(sheet, headers, fieldsFilter);
            }
            Row rowBody = sheet.createRow(rowIndex++);
            int fieldIndex = 0;
            for (Field field : fieldsFilter) {
                //设置访问权限修饰符级别
                field.setAccessible(true);
                //获取注解的属性value值，并设置单元格数据
                Object fieldValue = field.get(rowData);
                if (fieldValue instanceof Date && StringUtils.isNotEmpty(field.getAnnotation(Column.class).format())) {
                    rowBody.createCell(fieldIndex).setCellValue(DateFormatUtils.format(
                            (Date) fieldValue, field.getAnnotation(Column.class).format()));
                } else {
                    rowBody.createCell(fieldIndex).setCellValue(field.get(rowData).toString());
                }
                fieldIndex++;
            }
            count++;
        }
    }


    public static int processTitle(Sheet sheet, List<String> headers, List<Field> fieldsFilter) {
        //构建头信息
        int rowIndex = 0;
        if (headers != null && headers.size() > 0) {
            for (String header : headers) {
                Row rowHeader = sheet.createRow(rowIndex++);
                rowHeader.createCell(0).setCellValue(header);
            }
        }
        //根据反射获取注解中的列标题
        Row rowHeader = sheet.createRow(rowIndex++);
        int index = 0;
        for (Field field : fieldsFilter) {
            //获取注解的属性value值
            String value = field.getAnnotation(Column.class).value();
            rowHeader.createCell(index++).setCellValue(value);
        }
        return rowIndex;
    }

}
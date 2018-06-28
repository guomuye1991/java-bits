package com.common.excel;

import com.common.excel.model.CardExportDataModel;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExcelTest {

    public static void main(String args[]) throws Exception {
        List<String> headers = new ArrayList<>();
        headers.add("您正在使用指点无限-SaaS中心-卡券系统 生成的卡券");
        headers.add("名称：未来影院");
        headers.add("卡券类别：影院优惠卷");
        headers.add("有效期：365天");
        headers.add("入库渠道：测试渠道");
        headers.add("激活状态：已激活");
        headers.add("入库编号：11111111111");
        List<CardExportDataModel> body = new ArrayList<>();
        body.add(new CardExportDataModel("1", "a", "098", new Date()));
        body.add(new CardExportDataModel("2", "b", "098", new Date()));
        body.add(new CardExportDataModel("3", "c", "098", new Date()));
        String fileName = "卡劵-2017-06-02.xls";
        String sheetName = "卡劵列表";
        Workbook workbook = ApachePOIUtil.writeCountryListToFile(fileName, sheetName, headers, body, CardExportDataModel.class, 2);
        try (FileOutputStream fileOut = new FileOutputStream("./xxx" + fileName)) {
            workbook.write(fileOut);
        }
    }
}

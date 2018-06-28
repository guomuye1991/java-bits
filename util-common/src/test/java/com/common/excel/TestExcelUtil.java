package com.common.excel;

import lombok.Data;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TestExcelUtil {

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
        Workbook workbook = ExcelUtil.writeCountryListToFile(fileName, sheetName, headers, body, CardExportDataModel.class, 2);
        try (FileOutputStream fileOut = new FileOutputStream("./" + fileName)) {
            workbook.write(fileOut);
        }
    }
}

@Data
class CardExportDataModel {


    public CardExportDataModel(String index, String code, String pwd, Date date) {
        this.index = index;
        this.code = code;
        this.pwd = pwd;
        this.date = date;
    }

    @Column(value = "卡劵密码", ordered = 3)
    private String pwd;

    @Column(value = "序号", ordered = 1)
    private String index;

    @Column(value = "卡劵号", ordered = 2)
    private String code;

    @Column(value = "生成时间", ordered = 4, format = "yyyy-MM-dd HH:mm:ss")
    private Date date;


}

package com.common.excel.model;

import com.common.excel.Column;
import lombok.Data;

import java.util.Date;

@Data
public class CardExportDataModel {


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

package com.spec.dev;

public enum StatusCode {

    SUCCESS("00000", "成功");

    private String code;

    private String desc;

    StatusCode(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}

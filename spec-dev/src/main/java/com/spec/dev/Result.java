package com.spec.dev;

public class Result<T> {


    private String code;

    private String desc;

    private T data;


    public Result(StatusCode status) {
        code = status.getCode();
        desc = status.getDesc();
    }


    public Result(StatusCode status, T data) {
        this.data = data;
        code = status.getCode();
        desc = status.getDesc();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

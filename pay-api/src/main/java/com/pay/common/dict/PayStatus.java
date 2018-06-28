package com.pay.common.dict;

public class PayStatus {

    //处理中
    public static final Integer PROCESS = 0;
    //支付成功
    public static final Integer SUCCESS = 1;
    //支付失败
    public static final Integer FAIL = 2;
    //交易关闭 交易退款或交易取消
    public static final Integer CLOSE = 3;

}

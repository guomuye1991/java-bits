package com.pay.common;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PayParam {


    private String subject;

    private String desc;

    private LocalDateTime timeExpire;

    private LocalDateTime timeoutExpress;

    private BigDecimal money;

    private String outTradeNo;

    private String tradeNo;

    private BigDecimal refundAmount;

    private String refundReason;

    private String refundNo;

    private String key;

    private String signType;

    private String charset;

    private String payType;

    private String method;

    private String productId;

    private String openId;

    private String clientIp;

    private String notifyUrl;

    private String returnUrl;

}

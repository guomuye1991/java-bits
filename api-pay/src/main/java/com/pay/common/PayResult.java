package com.pay.common;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PayResult<T> {

    private Integer status;

    private String orderCode;

    private String tradeNo;

    private LocalDateTime payTime;

    private String desc;

    private BigDecimal money;

    private T rawData;

    private boolean check;


}

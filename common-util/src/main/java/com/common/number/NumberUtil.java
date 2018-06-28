package com.common.number;

import java.math.BigDecimal;

public class NumberUtil {

    public static String yuan2fen(BigDecimal num) {
        return String.valueOf(num.
                multiply(new BigDecimal(100))
                .longValue());
    }
}

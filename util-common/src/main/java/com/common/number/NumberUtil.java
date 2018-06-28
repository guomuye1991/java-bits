package com.common.number;

import java.math.BigDecimal;
import java.util.Random;

public class NumberUtil {

    public static String yuan2fen(BigDecimal num) {
        return String.valueOf(num.
                multiply(new BigDecimal(100))
                .longValue());
    }

    public static int generateNumberCode(byte bit) {
        int code = 0;
        Random random = new Random();
        for (byte i = 0; i < bit; i++) {
            code += Math.pow(10, i) * (random.nextInt(9) + 1);
        }
        return code;
    }
}

package com.common.id;

import java.util.UUID;

public class IdUtil {

    public static long currentTimeMillis() {
        return System.currentTimeMillis();
    }


    public static String uuid() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().replace("-", "");
    }

}

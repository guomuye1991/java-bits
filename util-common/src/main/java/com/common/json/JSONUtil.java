package com.common.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONUtil {


    private static final ObjectMapper OBJECTMAPPER = new ObjectMapper();


    public static final ObjectMapper getObjectMapper() {
        return OBJECTMAPPER;
    }

    public static String toStr(Object object) {
        try {
            return OBJECTMAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}

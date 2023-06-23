package com.bit.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Common {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static String objectToJson(Object o) {
        try {
            return mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            // TODO: alarm
            e.printStackTrace();
        }
        return "";
    }

    public static String objectToPrettyJson(Object o) {
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
        } catch (JsonProcessingException e) {
            // TODO: alarm
            e.printStackTrace();
        }
        return "";
    }

    public static String formatAsJson(String value) {
        try {
            var obj = mapper.readTree(value);
            return objectToPrettyJson(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return value;
        }
    }
}

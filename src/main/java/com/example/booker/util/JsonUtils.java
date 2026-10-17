package com.example.booker.util;

import com.example.booker.config.MapperConfig;

import java.io.IOException;

public final class JsonUtils {
    private JsonUtils() { }

    public static String toJson(Object value) {
        try {
            return MapperConfig.jsonMapper().writeValueAsString(value);
        } catch (IOException exception) {
            throw new IllegalArgumentException("Unable to serialize JSON", exception);
        }
    }

    public static <T> T fromJson(String value, Class<T> type) {
        try {
            return MapperConfig.jsonMapper().readValue(value, type);
        } catch (IOException exception) {
            throw new IllegalArgumentException("Unable to deserialize JSON as " + type.getSimpleName(), exception);
        }
    }
}

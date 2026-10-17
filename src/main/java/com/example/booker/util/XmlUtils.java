package com.example.booker.util;

import com.example.booker.config.MapperConfig;

import java.io.IOException;

public final class XmlUtils {
    private XmlUtils() { }

    public static String toXml(Object value) {
        try {
            return MapperConfig.xmlMapper().writeValueAsString(value);
        } catch (IOException exception) {
            throw new IllegalArgumentException("Unable to serialize XML", exception);
        }
    }

    public static <T> T fromXml(String value, Class<T> type) {
        try {
            return MapperConfig.xmlMapper().readValue(value, type);
        } catch (IOException exception) {
            throw new IllegalArgumentException("Unable to deserialize XML as " + type.getSimpleName(), exception);
        }
    }
}

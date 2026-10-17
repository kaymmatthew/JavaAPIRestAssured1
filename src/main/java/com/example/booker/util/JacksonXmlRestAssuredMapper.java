package com.example.booker.util;

import com.example.booker.config.MapperConfig;
import io.restassured.common.mapper.ObjectDeserializationContext;
import io.restassured.mapper.ObjectMapper;
import io.restassured.mapper.ObjectMapperDeserializationContext;
import io.restassured.mapper.ObjectMapperSerializationContext;

import java.io.IOException;

public final class JacksonXmlRestAssuredMapper implements ObjectMapper {
    public static final JacksonXmlRestAssuredMapper INSTANCE = new JacksonXmlRestAssuredMapper();

    private JacksonXmlRestAssuredMapper() { }

    @Override
    public Object serialize(ObjectMapperSerializationContext context) {
        try {
            return MapperConfig.xmlMapper().writeValueAsString(context.getObjectToSerialize());
        } catch (IOException exception) {
            throw new IllegalArgumentException("Unable to serialize request POJO as XML", exception);
        }
    }

    @Override
    public Object deserialize(ObjectMapperDeserializationContext context) {
        try {
            ObjectDeserializationContext data = context;
            return MapperConfig.xmlMapper().readValue(data.getDataToDeserialize().asString(),
                    MapperConfig.xmlMapper().constructType(data.getType()));
        } catch (IOException exception) {
            throw new IllegalArgumentException("Unable to deserialize XML response", exception);
        }
    }
}

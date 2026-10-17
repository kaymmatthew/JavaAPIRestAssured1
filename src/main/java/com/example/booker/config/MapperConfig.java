package com.example.booker.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.mapper.ObjectMapperType;

public final class MapperConfig {
    private static final ObjectMapper JSON = configureJson(new ObjectMapper());
    private static final XmlMapper XML = configureXml(XmlMapper.builder().build());

    private MapperConfig() { }

    public static ObjectMapper jsonMapper() {
        return JSON.copy();
    }

    public static XmlMapper xmlMapper() {
        return XML.copy();
    }

    public static ObjectMapperConfig restAssuredObjectMapperConfig() {
        return new ObjectMapperConfig()
                .defaultObjectMapperType(ObjectMapperType.JACKSON_2)
                .jackson2ObjectMapperFactory((type, charset) -> JSON.copy());
    }

    private static ObjectMapper configureJson(ObjectMapper mapper) {
        return mapper.registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    private static XmlMapper configureXml(XmlMapper mapper) {
        mapper.registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, false);
        return mapper;
    }
}

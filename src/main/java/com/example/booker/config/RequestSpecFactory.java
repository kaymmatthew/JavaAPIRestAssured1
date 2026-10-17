package com.example.booker.config;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import java.net.URI;
import java.net.http.HttpClient;
import java.time.Duration;

public final class RequestSpecFactory {
    private RequestSpecFactory() { }

    public static RequestSpecification json() {
        FrameworkConfig config = FrameworkConfig.get();
        return new RequestSpecBuilder()
                .setBaseUri(config.baseUrl())
                .setContentType(ContentType.JSON)
                .setAccept("application/json")
                .setConfig(io.restassured.config.RestAssuredConfig.config()
                        .objectMapperConfig(MapperConfig.restAssuredObjectMapperConfig())
                        .httpClient(io.restassured.config.HttpClientConfig.httpClientConfig()
                                .setParam("http.connection.timeout", Math.toIntExact(config.connectTimeout().toMillis()))
                                .setParam("http.socket.timeout", Math.toIntExact(config.readTimeout().toMillis()))))
                .addFilter(new RequestLoggingFilter(LogDetail.METHOD))
                .addFilter(new ResponseLoggingFilter(LogDetail.STATUS))
                .build();
    }

    public static RequestSpecification xml() {
        FrameworkConfig config = FrameworkConfig.get();
        return new RequestSpecBuilder()
                .setBaseUri(config.baseUrl())
                .setContentType("application/xml")
                .setAccept("application/xml")
                .setConfig(io.restassured.config.RestAssuredConfig.config()
                        .objectMapperConfig(MapperConfig.restAssuredObjectMapperConfig())
                        .httpClient(io.restassured.config.HttpClientConfig.httpClientConfig()
                                .setParam("http.connection.timeout", Math.toIntExact(config.connectTimeout().toMillis()))
                                .setParam("http.socket.timeout", Math.toIntExact(config.readTimeout().toMillis()))))
                .addFilter(new RequestLoggingFilter(LogDetail.METHOD))
                .addFilter(new ResponseLoggingFilter(LogDetail.STATUS))
                .build();
    }

    public static boolean isApiReachableWithinTimeout() {
        try {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(FrameworkConfig.get().connectTimeout())
                    .build();
            return client.send(java.net.http.HttpRequest.newBuilder()
                            .uri(URI.create(FrameworkConfig.get().baseUrl() + "/ping"))
                            .timeout(Duration.ofMillis(FrameworkConfig.get().maxResponseTimeMs()))
                            .GET().build(),
                    java.net.http.HttpResponse.BodyHandlers.discarding()).statusCode() == 201;
        } catch (Exception exception) {
            return false;
        }
    }
}

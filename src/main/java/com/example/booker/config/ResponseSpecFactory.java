package com.example.booker.config;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;

public final class ResponseSpecFactory {
    private ResponseSpecFactory() { }

    public static ResponseSpecification status(int expectedStatus) {
        return new ResponseSpecBuilder().expectStatusCode(expectedStatus).build();
    }
}

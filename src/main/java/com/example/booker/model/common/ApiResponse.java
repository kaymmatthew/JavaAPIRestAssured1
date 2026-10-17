package com.example.booker.model.common;

import io.restassured.response.Response;
import io.restassured.http.Headers;

public record ApiResponse<T>(int statusCode, T body, long responseTimeMs, Response rawResponse) {
    public ApiResponse {
        if (rawResponse == null) {
            throw new IllegalArgumentException("rawResponse is required");
        }
    }

    public Headers headers() {
        return rawResponse.getHeaders();
    }
}

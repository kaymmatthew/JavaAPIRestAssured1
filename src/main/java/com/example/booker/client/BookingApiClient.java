package com.example.booker.client;

import com.example.booker.config.RequestSpecFactory;
import com.example.booker.model.request.AuthRequest;
import com.example.booker.model.request.BookingPatchRequest;
import com.example.booker.model.request.BookingRequest;
import com.example.booker.model.common.ApiResponse;
import com.example.booker.model.response.AuthResponse;
import com.example.booker.model.response.BookingIdResponse;
import com.example.booker.model.response.BookingResponse;
import com.example.booker.model.response.CreateBookingResponse;
import com.example.booker.util.JacksonXmlRestAssuredMapper;
import com.example.booker.util.XmlUtils;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.List;
import java.util.Locale;

import static io.restassured.RestAssured.given;

public class BookingApiClient {
    public ApiResponse<AuthResponse> authenticate(AuthRequest request) {
        return execute(given().spec(RequestSpecFactory.json()).body(request).post("/auth"), AuthResponse.class);
    }

    public ApiResponse<CreateBookingResponse> createBooking(BookingRequest request) {
        return execute(given().spec(RequestSpecFactory.json()).body(request).post("/booking"),
                CreateBookingResponse.class);
    }

    public ApiResponse<CreateBookingResponse> createBookingXml(BookingRequest request) {
        Response response = given().spec(RequestSpecFactory.xml())
                .body(request, JacksonXmlRestAssuredMapper.INSTANCE).post("/booking");
        return executeXml(response, CreateBookingResponse.class);
    }

    public ApiResponse<BookingResponse> getBooking(int bookingId) {
        return execute(given().spec(RequestSpecFactory.json()).get("/booking/{id}", bookingId),
                BookingResponse.class);
    }

    public ApiResponse<BookingResponse> getBookingXml(int bookingId) {
        Response response = given().spec(RequestSpecFactory.xml()).get("/booking/{id}", bookingId);
        return executeXml(response, BookingResponse.class);
    }

    public ApiResponse<List<BookingIdResponse>> getBookingIds() {
        Response response = given().spec(RequestSpecFactory.json()).get("/booking");
        return new ApiResponse<>(response.statusCode(), response.jsonPath().getList(".", BookingIdResponse.class),
                response.time(), response);
    }

    public ApiResponse<BookingResponse> updateBooking(int bookingId, BookingRequest request, String token) {
        return execute(authenticated(token).body(request).put("/booking/{id}", bookingId), BookingResponse.class);
    }

    public ApiResponse<BookingResponse> patchBooking(int bookingId, BookingPatchRequest request, String token) {
        return execute(authenticated(token).body(request).patch("/booking/{id}", bookingId), BookingResponse.class);
    }

    public ApiResponse<Void> deleteBooking(int bookingId, String token) {
        Response response = authenticated(token).delete("/booking/{id}", bookingId);
        return new ApiResponse<>(response.statusCode(), null, response.time(), response);
    }

    public ApiResponse<Void> headBooking(int bookingId) {
        Response response = given().spec(RequestSpecFactory.json()).head("/booking/{id}", bookingId);
        return new ApiResponse<>(response.statusCode(), null, response.time(), response);
    }

    public ApiResponse<Void> optionsBooking(int bookingId) {
        Response response = given().spec(RequestSpecFactory.json()).options("/booking/{id}", bookingId);
        return new ApiResponse<>(response.statusCode(), null, response.time(), response);
    }

    public ApiResponse<Void> ping() {
        Response response = given().spec(RequestSpecFactory.json()).get("/ping");
        return new ApiResponse<>(response.statusCode(), null, response.time(), response);
    }

    public Response sendMalformedJson(String endpoint, String payload) {
        return given().spec(RequestSpecFactory.json()).body(payload).post(endpoint);
    }

    public Response sendWithContentType(String endpoint, Object payload, String contentType) {
        return given().spec(RequestSpecFactory.json()).contentType(contentType).body(payload).post(endpoint);
    }

    private RequestSpecification authenticated(String token) {
        return given().spec(RequestSpecFactory.json()).cookie("token", token);
    }

    private <T> ApiResponse<T> execute(Response response, Class<T> bodyType) {
        T body = response.getBody() == null || response.getBody().asString().isBlank() || !isJson(response)
                ? null : response.as(bodyType);
        return new ApiResponse<>(response.statusCode(), body, response.time(), response);
    }

    private <T> ApiResponse<T> executeXml(Response response, Class<T> bodyType) {
        String bodyText = response.getBody() == null ? "" : response.getBody().asString();
        T body = bodyText.isBlank() || response.statusCode() >= 400
                ? null : XmlUtils.fromXml(bodyText, bodyType);
        return new ApiResponse<>(response.statusCode(), body, response.time(), response);
    }

    private boolean isJson(Response response) {
        String contentType = response.getContentType();
        String normalized = contentType == null ? "" : contentType.toLowerCase(Locale.ROOT);
        return normalized.contains("application/json") || normalized.contains("+json");
    }
}

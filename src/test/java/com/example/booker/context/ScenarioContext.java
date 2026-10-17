package com.example.booker.context;

import com.example.booker.model.request.BookingRequest;
import com.example.booker.model.common.ApiResponse;
import com.example.booker.model.response.AuthResponse;
import com.example.booker.model.response.BookingResponse;
import com.example.booker.model.response.CreateBookingResponse;

public class ScenarioContext {
    private AuthResponse authResponse;
    private Integer bookingId;
    private BookingRequest bookingRequest;
    private ApiResponse<?> lastResponse;
    private BookingResponse bookingResponse;
    private ApiResponse<CreateBookingResponse> createBookingResponse;

    public AuthResponse authResponse() { return authResponse; }
    public void authResponse(AuthResponse authResponse) { this.authResponse = authResponse; }
    public String token() { return authResponse == null ? null : authResponse.getToken(); }
    public Integer bookingId() { return bookingId; }
    public void bookingId(Integer bookingId) { this.bookingId = bookingId; }
    public BookingRequest bookingRequest() { return bookingRequest; }
    public void bookingRequest(BookingRequest bookingRequest) { this.bookingRequest = bookingRequest; }
    public ApiResponse<?> lastResponse() { return lastResponse; }
    public void lastResponse(ApiResponse<?> lastResponse) { this.lastResponse = lastResponse; }
    public BookingResponse bookingResponse() { return bookingResponse; }
    public void bookingResponse(BookingResponse bookingResponse) { this.bookingResponse = bookingResponse; }
    public ApiResponse<CreateBookingResponse> createBookingResponse() { return createBookingResponse; }
    public void createBookingResponse(ApiResponse<CreateBookingResponse> response) { this.createBookingResponse = response; }
}

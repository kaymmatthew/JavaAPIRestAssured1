package com.example.booker.service;

import com.example.booker.client.BookingApiClient;
import com.example.booker.model.request.AuthRequest;
import com.example.booker.model.request.BookingRequest;
import com.example.booker.model.common.ApiResponse;
import com.example.booker.model.response.AuthResponse;
import com.example.booker.model.response.BookingResponse;
import com.example.booker.model.response.CreateBookingResponse;
import com.example.booker.util.LogUtils;
import org.slf4j.Logger;

public class BookingService {
    private static final Logger LOGGER = LogUtils.logger(BookingService.class);
    private final BookingApiClient client;

    public BookingService() {
        this(new BookingApiClient());
    }

    public BookingService(BookingApiClient client) {
        this.client = client;
    }

    public ApiResponse<CreateBookingResponse> createAuthenticatedBooking(BookingRequest request,
                                                                         AuthRequest credentials) {
        ApiResponse<AuthResponse> authResponse = client.authenticate(credentials);
        LOGGER.debug("Authentication completed with status {}", authResponse.statusCode());
        if (authResponse.statusCode() != 200 || authResponse.body() == null
                || authResponse.body().getToken() == null) {
            throw new IllegalStateException("Authentication failed: status " + authResponse.statusCode());
        }
        ApiResponse<CreateBookingResponse> bookingResponse = client.createBooking(request);
        LOGGER.debug("Booking creation completed with status {}", bookingResponse.statusCode());
        if (bookingResponse.statusCode() != 200 || bookingResponse.body() == null
                || bookingResponse.body().getBookingId() == null) {
            throw new IllegalStateException("Booking creation failed: status " + bookingResponse.statusCode());
        }
        return bookingResponse;
    }

    public BookingResponse requireBooking(int bookingId) {
        ApiResponse<BookingResponse> response = client.getBooking(bookingId);
        if (response.statusCode() != 200 || response.body() == null) {
            throw new IllegalStateException("Booking retrieval failed: status " + response.statusCode());
        }
        return response.body();
    }
}

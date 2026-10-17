package com.example.booker.builder;

import com.example.booker.model.request.BookingDates;
import com.example.booker.model.request.BookingRequest;
import com.github.javafaker.Faker;

import java.time.LocalDate;

public final class BookingRequestBuilder {
    private static final Faker FAKER = new Faker();
    private BookingRequestBuilder() { }

    public static BookingRequest valid() {
        LocalDate checkin = LocalDate.now().plusDays(7);
        return BookingRequest.builder()
                .firstName(FAKER.name().firstName())
                .lastName(FAKER.name().lastName())
                .totalPrice(FAKER.number().numberBetween(50, 1000))
                .depositPaid(FAKER.bool().bool())
                .bookingDates(BookingDates.builder().checkin(checkin).checkout(checkin.plusDays(3)).build())
                .additionalNeeds("Breakfast")
                .build();
    }

    public static BookingRequest invalidMissingFirstName() {
        BookingRequest valid = valid();
        valid.setFirstName(null);
        return valid;
    }
}

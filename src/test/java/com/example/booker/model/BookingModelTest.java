package com.example.booker.model;

import com.example.booker.config.MapperConfig;
import com.example.booker.model.request.AuthRequest;
import com.example.booker.model.request.BookingDates;
import com.example.booker.model.request.BookingPatchRequest;
import com.example.booker.model.request.BookingRequest;
import com.example.booker.model.response.BookingResponse;
import com.example.booker.model.response.AuthResponse;
import com.example.booker.model.response.BookingDatesResponse;
import com.example.booker.model.response.BookingIdResponse;
import com.example.booker.model.response.CreateBookingResponse;
import com.example.booker.model.response.ErrorResponse;
import com.example.booker.util.JsonUtils;
import com.example.booker.util.XmlUtils;
import org.junit.jupiter.api.Test;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import java.io.InputStream;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BookingModelTest {
    @Test
    void jsonFixtureRoundTripsToTypedResponseAndIgnoresUnknownResponseFields() throws Exception {
        try (InputStream fixture = resource("fixtures/booking-response.json")) {
            BookingResponse response = MapperConfig.jsonMapper().readValue(fixture, BookingResponse.class);
            assertThat(response.getFirstName()).isEqualTo("Ada");
            assertThat(response.getBookingDates().getCheckin()).isEqualTo(LocalDate.of(2026, 11, 1));
            assertThat(JsonUtils.fromJson(JsonUtils.toJson(response), BookingResponse.class))
                    .usingRecursiveComparison().isEqualTo(response);
        }
    }

    @Test
    void xmlFixtureDeserializesAndPassesThePublishedXsd() throws Exception {
        try (InputStream fixture = resource("fixtures/booking-response.xml")) {
            BookingResponse response = MapperConfig.xmlMapper().readValue(fixture, BookingResponse.class);
            assertThat(response.getLastName()).isEqualTo("Lovelace");
            assertThat(response.getBookingDates().getCheckout()).isEqualTo(LocalDate.of(2026, 11, 5));
        }
        SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
                .newSchema(getClass().getClassLoader().getResource("schemas/booking-response.xsd"))
                .newValidator().validate(new StreamSource(resource("fixtures/booking-response.xml")));
    }

    @Test
    void bookingRequestSerializesToXmlAndRoundTripsWithTypedDates() {
        BookingRequest request = BookingRequest.builder().firstName("Katherine").lastName("Johnson")
                .totalPrice(310).depositPaid(true)
                .bookingDates(BookingDates.builder().checkin(LocalDate.of(2026, 12, 2))
                        .checkout(LocalDate.of(2026, 12, 6)).build())
                .additionalNeeds("Airport pickup").build();
        String xml = XmlUtils.toXml(request);
        assertThat(xml).contains("<booking>", "<firstname>Katherine</firstname>",
                "<checkin>2026-12-02</checkin>");
        assertThat(XmlUtils.fromXml(xml, BookingResponse.class))
                .usingRecursiveComparison().isEqualTo(request);
    }

    @Test
    void patchSerializationOmitsEveryUnsetPropertyAndFormatsDatesAsIsoStrings() {
        BookingPatchRequest patch = BookingPatchRequest.builder().firstName("Grace")
                .bookingDates(BookingDates.builder().checkin(LocalDate.of(2026, 12, 1)).build()).build();
        String json = JsonUtils.toJson(patch);
        assertThat(json).contains("\"firstname\":\"Grace\"", "\"checkin\":\"2026-12-01\"")
                .doesNotContain("lastname", "totalprice", "depositpaid", "checkout", "additionalneeds");
    }

    @Test
    void authRequestNeverExposesPasswordInGeneratedToString() {
        AuthRequest request = AuthRequest.builder().username("admin").password("top-secret").build();
        assertThat(request.toString()).doesNotContain("top-secret").contains("admin");
    }

    @Test
    void everyRequestAndResponsePojoRoundTripsThroughJson() throws Exception {
        BookingDates dates = BookingDates.builder().checkin(LocalDate.of(2026, 11, 1))
                .checkout(LocalDate.of(2026, 11, 5)).build();
        BookingRequest booking = BookingRequest.builder().firstName("Ada").lastName("Lovelace")
                .totalPrice(125).depositPaid(true).bookingDates(dates).additionalNeeds("Breakfast").build();
        BookingResponse bookingResponse = new BookingResponse("Ada", "Lovelace", 125, true,
                new BookingDatesResponse(LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 5)), "Breakfast");
        assertJsonRoundTrip(AuthRequest.builder().username("admin").password("password").build(), AuthRequest.class);
        assertJsonRoundTrip(dates, BookingDates.class);
        assertJsonRoundTrip(booking, BookingRequest.class);
        assertJsonRoundTrip(BookingPatchRequest.builder().firstName("Grace").build(), BookingPatchRequest.class);
        assertJsonRoundTrip(new AuthResponse("token-value", null), AuthResponse.class);
        assertJsonRoundTrip(new BookingDatesResponse(LocalDate.of(2026, 11, 1),
                LocalDate.of(2026, 11, 5)), BookingDatesResponse.class);
        assertJsonRoundTrip(bookingResponse, BookingResponse.class);
        assertJsonRoundTrip(new CreateBookingResponse(42, bookingResponse), CreateBookingResponse.class);
        assertJsonRoundTrip(new BookingIdResponse(42), BookingIdResponse.class);
        assertJsonRoundTrip(new ErrorResponse("Not Found", "Booking does not exist"), ErrorResponse.class);
    }

    @Test
    void jsonMapperFailsOnUnknownPropertiesForUnannotatedRequestModels() {
        assertThatThrownBy(() -> MapperConfig.jsonMapper()
                .readValue("{\"unexpected\":true}", BookingRequest.class))
                .isInstanceOf(com.fasterxml.jackson.databind.JsonMappingException.class);
        assertThatThrownBy(() -> MapperConfig.jsonMapper()
                .readValue("{\"bookingdates\":{\"checkin\":\"2026-11-01\",\"unknown\":true}}",
                        BookingRequest.class))
                .isInstanceOf(com.fasterxml.jackson.databind.JsonMappingException.class);
    }

    private InputStream resource(String name) {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(name);
        assertThat(stream).as("fixture resource %s", name).isNotNull();
        return stream;
    }

    private <T> void assertJsonRoundTrip(T value, Class<T> type) throws Exception {
        assertThat(MapperConfig.jsonMapper().readValue(MapperConfig.jsonMapper().writeValueAsString(value), type))
                .usingRecursiveComparison().isEqualTo(value);
    }
}

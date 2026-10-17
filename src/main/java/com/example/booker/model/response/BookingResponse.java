package com.example.booker.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JacksonXmlRootElement(localName = "booking")
public class BookingResponse {
    @JsonProperty("firstname")
    private String firstName;
    @JsonProperty("lastname")
    private String lastName;
    @JsonProperty("totalprice")
    private Integer totalPrice;
    @JsonProperty("depositpaid")
    private Boolean depositPaid;
    @JsonProperty("bookingdates")
    private BookingDatesResponse bookingDates;
    @JsonProperty("additionalneeds")
    private String additionalNeeds;
}

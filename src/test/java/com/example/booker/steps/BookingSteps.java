package com.example.booker.steps;

import com.example.booker.client.BookingApiClient;
import com.example.booker.config.FrameworkConfig;
import com.example.booker.context.ScenarioContext;
import com.example.booker.model.request.AuthRequest;
import com.example.booker.model.request.BookingPatchRequest;
import com.example.booker.model.common.ApiResponse;
import com.example.booker.model.response.AuthResponse;
import com.example.booker.model.response.BookingResponse;
import com.example.booker.model.response.CreateBookingResponse;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;

import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

public class BookingSteps {
    private final ScenarioContext context;
    private final BookingApiClient client = new BookingApiClient();

    public BookingSteps(ScenarioContext context) {
        this.context = context;
    }

    @Given("a valid booking payload")
    public void aValidBookingPayload() {
        context.bookingRequest(com.example.booker.builder.BookingRequestBuilder.valid());
    }

    @When("I create the booking")
    public void iCreateTheBooking() {
        ApiResponse<CreateBookingResponse> response = client.createBooking(context.bookingRequest());
        context.createBookingResponse(response);
        context.lastResponse(response);
        if (response.body() != null) {
            context.bookingId(response.body().getBookingId());
        }
    }

    @Then("the booking is created successfully")
    public void theBookingIsCreatedSuccessfully() {
        assertThat(context.lastResponse().statusCode()).isEqualTo(200);
        assertThat(context.bookingId()).isNotNull();
        assertThat(context.bookingRequest().getBookingDates().getCheckin())
                .isBefore(context.bookingRequest().getBookingDates().getCheckout());
        assertBookingMatches(context.createBookingResponse().body().getBooking(), context.bookingRequest());
    }

    @Then("the booking response matches the JSON schema")
    public void theBookingResponseMatchesTheJsonSchema() {
        try (InputStream schema = getClass().getClassLoader().getResourceAsStream("schemas/booking-response.schema.json")) {
            assertThat(schema).as("booking JSON schema resource").isNotNull();
            context.lastResponse().rawResponse().then().body(JsonSchemaValidator.matchesJsonSchema(schema));
            assertThat(context.lastResponse().rawResponse().jsonPath().getString("booking.firstname"))
                    .isEqualTo(context.bookingRequest().getFirstName());
        } catch (Exception exception) {
            throw new AssertionError("Unable to validate booking response schema", exception);
        }
    }

    @When("I retrieve the created booking")
    public void iRetrieveTheCreatedBooking() {
        ApiResponse<BookingResponse> response = client.getBooking(context.bookingId());
        context.lastResponse(response);
        context.bookingResponse(response.body());
    }

    @Then("the retrieved booking matches the submitted booking")
    public void theRetrievedBookingMatchesTheSubmittedBooking() {
        assertThat(context.lastResponse().statusCode()).isEqualTo(200);
        assertBookingMatches(context.bookingResponse(), context.bookingRequest());
        assertThat(context.lastResponse().rawResponse().jsonPath().getString("firstname"))
                .isEqualTo(context.bookingRequest().getFirstName());
    }

    @When("I authenticate with valid credentials")
    public void iAuthenticateWithValidCredentials() {
        ApiResponse<AuthResponse> response = client.authenticate(com.example.booker.builder.AuthRequestBuilder.valid());
        context.lastResponse(response);
        context.authResponse(response.body());
    }

    @When("I authenticate with an incorrect password")
    public void iAuthenticateWithAnIncorrectPassword() {
        context.lastResponse(client.authenticate(com.example.booker.builder.AuthRequestBuilder.invalidPassword()));
    }

    @When("I authenticate without credentials")
    public void iAuthenticateWithoutCredentials() {
        context.lastResponse(client.authenticate(com.example.booker.builder.AuthRequestBuilder.missingCredentials()));
    }

    @Then("authentication succeeds with a token")
    public void authenticationSucceedsWithAToken() {
        assertThat(context.lastResponse().statusCode()).isEqualTo(200);
        assertThat(context.token()).isNotBlank();
    }

    @Then("authentication is rejected")
    public void authenticationIsRejected() {
        assertThat(context.lastResponse().statusCode()).isEqualTo(200);
        AuthResponse body = (AuthResponse) context.lastResponse().body();
        assertThat(body.getToken()).isNull();
        assertThat(body.getReason()).isNotBlank();
    }

    @When("I partially update the first name")
    public void iPartiallyUpdateTheFirstName() {
        ApiResponse<AuthResponse> auth = client.authenticate(com.example.booker.builder.AuthRequestBuilder.valid());
        assertThat(auth.body()).isNotNull();
        context.authResponse(auth.body());
        String replacement = context.bookingRequest().getFirstName() + "Updated";
        context.lastResponse(client.patchBooking(context.bookingId(),
                BookingPatchRequest.builder().firstName(replacement).build(), context.token()));
        context.bookingRequest().setFirstName(replacement);
    }

    @Then("the partial update preserves other booking fields")
    public void thePartialUpdatePreservesOtherBookingFields() {
        assertThat(context.lastResponse().statusCode()).isEqualTo(200);
        BookingResponse body = (BookingResponse) context.lastResponse().body();
        assertThat(body.getFirstName()).isEqualTo(context.bookingRequest().getFirstName());
        assertThat(body.getLastName()).isEqualTo(context.bookingRequest().getLastName());
        assertThat(body.getBookingDates().getCheckin())
                .isEqualTo(context.bookingRequest().getBookingDates().getCheckin());
        assertThat(body.getBookingDates().getCheckout())
                .isEqualTo(context.bookingRequest().getBookingDates().getCheckout());
        assertThat(body.getTotalPrice()).isEqualTo(context.bookingRequest().getTotalPrice());
    }

    @When("I replace the booking")
    public void iReplaceTheBooking() {
        ApiResponse<AuthResponse> auth = client.authenticate(com.example.booker.builder.AuthRequestBuilder.valid());
        context.authResponse(auth.body());
        context.lastResponse(client.updateBooking(context.bookingId(), context.bookingRequest(), context.token()));
    }

    @Then("the full update returns the replacement booking")
    public void theFullUpdateReturnsTheReplacementBooking() {
        assertThat(context.lastResponse().statusCode()).isEqualTo(200);
        assertBookingMatches((BookingResponse) context.lastResponse().body(), context.bookingRequest());
    }

    @When("I delete the created booking")
    public void iDeleteTheCreatedBooking() {
        ApiResponse<AuthResponse> auth = client.authenticate(com.example.booker.builder.AuthRequestBuilder.valid());
        context.authResponse(auth.body());
        context.lastResponse(client.deleteBooking(context.bookingId(), context.token()));
    }

    @When("I retrieve the created booking as XML")
    public void iRetrieveTheCreatedBookingAsXml() {
        ApiResponse<BookingResponse> response = client.getBookingXml(context.bookingId());
        context.lastResponse(response);
        context.bookingResponse(response.body());
    }

    @Then("the XML booking matches the submitted booking and its schema")
    public void theXmlBookingMatchesTheSubmittedBookingAndItsSchema() throws Exception {
        assertThat(context.lastResponse().statusCode()).isEqualTo(200);
        assertBookingMatches(context.bookingResponse(), context.bookingRequest());
        try (InputStream schema = getClass().getClassLoader().getResourceAsStream("schemas/booking-response.xsd")) {
            assertThat(schema).as("booking XML schema resource").isNotNull();
            javax.xml.validation.SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI)
                    .newSchema(new javax.xml.transform.stream.StreamSource(schema))
                    .newValidator().validate(new javax.xml.transform.stream.StreamSource(
                            new java.io.StringReader(context.lastResponse().rawResponse().asString())));
        }
    }

    @When("I send a booking update with an invalid token")
    public void iSendABookingUpdateWithAnInvalidToken() {
        context.lastResponse(client.patchBooking(context.bookingId(),
                BookingPatchRequest.builder().firstName("Unauthorized").build(), "invalid-token"));
    }

    @Then("the API denies the protected update")
    public void theApiDeniesTheProtectedUpdate() {
        assertThat(context.lastResponse().statusCode()).isIn(401, 403);
    }

    @Then("the booking is deleted")
    public void theBookingIsDeleted() {
        assertThat(context.lastResponse().statusCode()).isIn(201, 204);
        assertThat(client.getBooking(context.bookingId()).statusCode()).isEqualTo(404);
    }

    @When("I request a nonexistent booking")
    public void iRequestANonexistentBooking() {
        context.lastResponse(client.getBooking(Integer.MAX_VALUE));
    }

    @Then("the API reports that the booking was not found")
    public void theApiReportsThatTheBookingWasNotFound() {
        assertThat(context.lastResponse().statusCode()).isEqualTo(404);
    }

    @When("I send a malformed JSON booking request")
    public void iSendAMalformedJsonBookingRequest() {
        Response response = client.sendMalformedJson("/booking", "{\"firstname\":");
        context.lastResponse(new ApiResponse<>(response.statusCode(), null, response.time(), response));
    }

    @When("I create a booking with required fields missing")
    public void iCreateABookingWithRequiredFieldsMissing() {
        Response response = client.sendWithContentType("/booking",
                com.example.booker.builder.BookingRequestBuilder.invalidMissingFirstName(), "application/json");
        context.lastResponse(new ApiResponse<>(response.statusCode(), null, response.time(), response));
    }

    @When("I send a booking body with an unsupported content type")
    public void iSendABookingBodyWithAnUnsupportedContentType() {
        Response response = client.sendWithContentType("/booking",
                "{\"firstname\":\"Taylor\",\"lastname\":\"Example\"}", "text/plain");
        context.lastResponse(new ApiResponse<>(response.statusCode(), null, response.time(), response));
    }

    @Then("the API rejects the malformed request")
    public void theApiRejectsTheMalformedRequest() {
        assertThat(context.lastResponse().statusCode()).isBetween(400, 599);
    }

    @Then("the response time is within the configured limit")
    public void theResponseTimeIsWithinTheConfiguredLimit() {
        assertThat(context.lastResponse().responseTimeMs())
                .isLessThanOrEqualTo(FrameworkConfig.get().maxResponseTimeMs());
    }

    @When("I send an OPTIONS request for the booking")
    public void iSendAnOptionsRequestForTheBooking() {
        context.lastResponse(client.optionsBooking(context.bookingId()));
    }

    @When("I send a HEAD request for the booking")
    public void iSendAHeadRequestForTheBooking() {
        context.lastResponse(client.headBooking(context.bookingId()));
    }

    @Then("the endpoint reports a standard supported or unsupported method response")
    public void theEndpointReportsAStandardSupportedOrUnsupportedMethodResponse() {
        assertThat(context.lastResponse().statusCode()).isBetween(200, 599);
    }

    private void assertBookingMatches(BookingResponse actual,
                                     com.example.booker.model.request.BookingRequest expected) {
        assertThat(actual.getFirstName()).isEqualTo(expected.getFirstName());
        assertThat(actual.getLastName()).isEqualTo(expected.getLastName());
        assertThat(actual.getTotalPrice()).isEqualTo(expected.getTotalPrice());
        assertThat(actual.getDepositPaid()).isEqualTo(expected.getDepositPaid());
        assertThat(actual.getAdditionalNeeds()).isEqualTo(expected.getAdditionalNeeds());
        assertThat(actual.getBookingDates().getCheckin()).isEqualTo(expected.getBookingDates().getCheckin());
        assertThat(actual.getBookingDates().getCheckout()).isEqualTo(expected.getBookingDates().getCheckout());
    }
}

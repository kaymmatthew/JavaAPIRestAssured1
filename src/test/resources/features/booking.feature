Feature: Restful Booker booking API

  Background:
    Given a valid booking payload

  @api
  Scenario: Create a booking and validate its representation
    When I create the booking
    Then the booking is created successfully
    And the booking response matches the JSON schema
    And the response time is within the configured limit
    When I retrieve the created booking
    Then the retrieved booking matches the submitted booking

  @api
  Scenario: Retrieve a booking as XML and validate its representation
    When I create the booking
    Then the booking is created successfully
    When I retrieve the created booking as XML
    Then the XML booking matches the submitted booking and its schema

  @api
  Scenario: Reject a protected update with an invalid token
    When I create the booking
    Then the booking is created successfully
    When I send a booking update with an invalid token
    Then the API denies the protected update

  @api
  Scenario: Partially update a booking without changing the rest
    When I create the booking
    Then the booking is created successfully
    When I partially update the first name
    Then the partial update preserves other booking fields

  @api
  Scenario: Fully replace a booking
    When I create the booking
    Then the booking is created successfully
    When I replace the booking
    Then the full update returns the replacement booking

  @api
  Scenario: Delete a booking
    When I create the booking
    Then the booking is created successfully
    When I delete the created booking
    Then the booking is deleted

  @api
  Scenario Outline: Reject absent or incorrect authentication credentials
    When I authenticate <credentials>
    Then authentication is rejected

    Examples:
      | credentials             |
      | with an incorrect password |
      | without credentials        |

  @api
  Scenario: Authenticate with valid credentials
    When I authenticate with valid credentials
    Then authentication succeeds with a token

  @api
  Scenario: Report an unknown booking
    When I request a nonexistent booking
    Then the API reports that the booking was not found

  @api
  Scenario: Reject malformed JSON
    When I send a malformed JSON booking request
    Then the API rejects the malformed request

  @api
  Scenario: Reject a booking missing required fields
    When I create a booking with required fields missing
    Then the API rejects the malformed request

  @api
  Scenario: Reject a booking with an unsupported content type
    When I send a booking body with an unsupported content type
    Then the API rejects the malformed request

  @api
  Scenario: Answer OPTIONS requests without assuming support
    When I create the booking
    Then the booking is created successfully
    When I send an OPTIONS request for the booking
    Then the endpoint reports a standard supported or unsupported method response

  @api
  Scenario: Answer HEAD requests without assuming support
    When I create the booking
    Then the booking is created successfully
    When I send a HEAD request for the booking
    Then the endpoint reports a standard supported or unsupported method response

# Restful Booker API automation

Java 17 Maven test framework for [Restful Booker](https://restful-booker.herokuapp.com), built with REST Assured, Cucumber 7/JUnit Platform, Jackson, Lombok, AssertJ, JavaFaker and Allure.

## Requirements and commands

Install JDK 17 and Maven 3.8 or newer. Run the complete suite with:

```shell
mvn clean verify
```

Choose an environment, override its URL, filter scenarios, and configure the response-time budget with:

```shell
mvn test -Denv=stage -DbaseUrl=https://restful-booker.herokuapp.com \
  -Dcucumber.filter.tags="@api and not @wip" -DmaxResponseTimeMs=5000
```

`dev`, `stage`, and `prod` configuration files are under `src/main/resources/config`. The demo currently uses the same public host for all three; override `baseUrl` for a controlled deployment. The API scenarios are tagged `@api` and excluded from the default test run so `mvn test` and CI stay deterministic when the shared demo host is unavailable or rate-limits traffic. Run them explicitly with `-Dcucumber.filter.tags="@api"` when the service is available.

## Layout

- `model/request`, `model/response`, and `model/common`: typed JSON/XML-compatible POJOs and generic response envelope
- `client` and `service`: REST Assured transport and reusable domain operations
- `config`: environment loading, thread-safe mapper factories, timeouts and request/response specifications
- `src/test/resources/features`: executable Gherkin scenarios; PicoContainer creates isolated context per scenario
- `src/main/resources/schemas` and `src/test/resources/fixtures`: response schemas and offline examples
- `builder`, `context`, `steps`, `runner`: generated test data, scenario state and Cucumber glue

Requests omit null values; credentials are not included in generated `toString()` output and request/response logs expose only method/status, never bodies. JSON and XML mappers are separately configured, use `JavaTimeModule`, emit ISO dates, and reject unknown fields by default. Response models intentionally ignore future server fields. REST Assured always serializes typed POJOs using the configured Jackson JSON mapper. For deliberate malformed-payload testing only, the client has a string-payload helper.

## Reports

Cucumber HTML and JSON reports are written to `target/cucumber-reports`; Allure results are written to `target/allure-results`. Generate an Allure report when the Allure CLI is installed using `allure serve target/allure-results`.

The functional suite avoids depending on a fixed shared booking ID: it creates scenario-specific records and checks the public API's actual response behavior. Optional method support accepts a normal HTTP status response rather than assuming the server implements it.

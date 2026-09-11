package org.example.rebalancing.tests;

import org.example.rebalancing.config.WireMockConfig;
import org.example.rebalancing.mock.RebalancingApiMock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;

class RebalancingApiTest {

    private static WireMockConfig wireMockConfig;

    @BeforeAll
    static void setUp() {

        wireMockConfig = new WireMockConfig();

        wireMockConfig.start();

        RebalancingApiMock apiMock =
                new RebalancingApiMock(wireMockConfig);

        apiMock.stubSuccessfulRebalance();
    }

    @AfterAll
    static void tearDown() {

        wireMockConfig.stop();
    }

    @Test
    void shouldCorrectlyRebalanceAccountABC() {

        String requestBody = loadRequestFromClasspath("account-abc.json");

        given()
                .baseUri(wireMockConfig.getBaseUrl())
                .contentType("application/json")
                .body(requestBody)

                .when()
                .post("/rebalance")

                .then()
                .statusCode(200)

                .body(
                        "accountId",
                        equalTo("ABC")
                )

                .body(
                        "rebalance.size()",
                        equalTo(5)
                )

                .body(
                        "rebalance.find { it.symbol == 'IBM' }.action",
                        equalTo("BUY")
                )

                .body(
                        "rebalance.find { it.symbol == 'IBM' }.shares",
                        closeTo(66.6667d, 0.0001d)
                )

                .body(
                        "rebalance.find { it.symbol == 'MSFT' }.action",
                        equalTo("NONE")
                )

                .body(
                        "rebalance.find { it.symbol == 'MSFT' }.shares",
                        equalTo(0)
                )

                .body(
                        "rebalance.find { it.symbol == 'ORCL' }.action",
                        equalTo("SELL")
                )

                .body(
                        "rebalance.find { it.symbol == 'ORCL' }.shares",
                        closeTo(45.4545d, 0.0001d)
                )

                .body(
                        "rebalance.find { it.symbol == 'AAPL' }.action",
                        equalTo("NONE")
                )

                .body(
                        "rebalance.find { it.symbol == 'AAPL' }.shares",
                        equalTo(0)
                )

                .body(
                        "rebalance.find { it.symbol == 'HD' }.action",
                        equalTo("NONE")
                )

                .body(
                        "rebalance.find { it.symbol == 'HD' }.shares",
                        equalTo(0)
                );
    }

    private static String loadRequestFromClasspath(String filename) {

        try {
            var resource = RebalancingApiTest.class
                    .getClassLoader()
                    .getResourceAsStream(filename);

            if (resource == null) {
                throw new IllegalArgumentException(
                        "Resource not found on classpath: " + filename
                );
            }

            return new String(resource.readAllBytes(), StandardCharsets.UTF_8);

        } catch (IOException e) {

            throw new RuntimeException(
                    "Could not read request file: " + filename,
                    e
            );
        }
    }
}
package org.example.rebalancing.mock;

import org.example.rebalancing.config.WireMockConfig;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class RebalancingApiMock {

    private final WireMockConfig config;

    public RebalancingApiMock(WireMockConfig config) {
        this.config = config;
    }

    public void stubSuccessfulRebalance() {

        configureFor("localhost", 8080);

        String responseBody = loadResponseFromClasspath("account-abc-response.json");

        stubFor(
                post(urlEqualTo("/rebalance"))
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withHeader(
                                                "Content-Type",
                                                "application/json"
                                        )
                                        .withBody(responseBody)
                        )
        );
    }

    private String loadResponseFromClasspath(String filename) {

        try {
            var resource = getClass().getClassLoader().getResourceAsStream(filename);

            if (resource == null) {
                throw new IllegalArgumentException(
                        "Resource not found on classpath: " + filename
                );
            }

            return new String(resource.readAllBytes(), StandardCharsets.UTF_8);

        } catch (IOException e) {

            throw new RuntimeException(
                    "Could not read mock response file: " + filename,
                    e
            );
        }
    }
}
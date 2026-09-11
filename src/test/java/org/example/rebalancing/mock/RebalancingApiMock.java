package org.example.rebalancing.mock;

import org.example.rebalancing.config.ApiConstants;
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
        configureFor(ApiConstants.HOST, ApiConstants.WIREMOCK_PORT);

        String responseBody = loadResponseFromClasspath(ApiConstants.ACCOUNT_ABC_RESPONSE);

        stubFor(
                post(urlEqualTo(ApiConstants.REBALANCE_ENDPOINT))
                        .willReturn(
                                aResponse()
                                        .withStatus(ApiConstants.HTTP_OK)
                                        .withHeader("Content-Type", ApiConstants.CONTENT_TYPE_JSON)
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
package org.example.rebalancing.tests;

import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import org.example.rebalancing.config.ApiConstants;
import org.example.rebalancing.config.WireMockConfig;
import org.example.rebalancing.mock.RebalancingApiMock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class ApiTestBase {

    protected static WireMockConfig wireMockConfig;
    protected static RebalancingApiMock apiMock;

    @BeforeAll
    static void setUpBase() {
        System.out.println("[SETUP] Initializing WireMock server...");

        wireMockConfig = new WireMockConfig();
        wireMockConfig.start();

        apiMock = new RebalancingApiMock(wireMockConfig);

        RestAssured.config = RestAssured.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", ApiConstants.CONNECTION_TIMEOUT_MS)
                        .setParam("http.socket.timeout", ApiConstants.SOCKET_TIMEOUT_MS));

        System.out.println("[SETUP] WireMock server started on " + wireMockConfig.getBaseUrl());
    }

    @AfterAll
    static void tearDownBase() {
        System.out.println("[TEARDOWN] Stopping WireMock server...");

        if (wireMockConfig != null) {
            wireMockConfig.stop();
        }

        System.out.println("[TEARDOWN] WireMock server stopped");
    }

    protected static String loadResourceFromClasspath(String filename) {
        try {
            var resource = ApiTestBase.class
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
                    "Could not read resource file: " + filename,
                    e
            );
        }
    }

    protected static String getBaseUrl() {
        return wireMockConfig.getBaseUrl();
    }
}
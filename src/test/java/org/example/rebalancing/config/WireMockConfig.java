package org.example.rebalancing.config;

import com.github.tomakehurst.wiremock.WireMockServer;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

/**
 * Manages WireMock server lifecycle (start/stop).
 * Uses constants from ApiConstants for configuration.
 */
public class WireMockConfig {

    private WireMockServer wireMockServer;

    /**
     * Start the WireMock server on the configured port.
     */
    public void start() {
        wireMockServer = new WireMockServer(
                wireMockConfig().port(ApiConstants.WIREMOCK_PORT)
        );

        wireMockServer.start();
    }

    /**
     * Stop the WireMock server.
     */
    public void stop() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    /**
     * Get the base URL of the WireMock server.
     *
     * @return Base URL (e.g., http://localhost:8080)
     */
    public String getBaseUrl() {
        return ApiConstants.BASE_URL;
    }
}

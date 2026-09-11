package org.example.rebalancing.config;

import com.github.tomakehurst.wiremock.WireMockServer;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

public class WireMockConfig {

    private WireMockServer wireMockServer;

    public void start() {
        wireMockServer = new WireMockServer(
                wireMockConfig().port(8080)
        );

        wireMockServer.start();
    }

    public void stop() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    public String getBaseUrl() {
        return "http://localhost:8080";
    }
}
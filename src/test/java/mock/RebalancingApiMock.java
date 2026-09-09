package mock;

import config.WireMockConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class RebalancingApiMock {

    private final WireMockConfig config;

    public RebalancingApiMock(WireMockConfig config) {
        this.config = config;
    }

    public void stubSuccessfulRebalance() {

        configureFor("localhost", 8080);

        String responseBody = loadResponse(
                "src/test/resources/account-abc-response.json"
        );

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

    private String loadResponse(String filePath) {

        try {
            return Files.readString(Path.of(filePath));

        } catch (IOException e) {

            throw new RuntimeException(
                    "Could not read mock response file: " + filePath,
                    e
            );
        }
    }
}
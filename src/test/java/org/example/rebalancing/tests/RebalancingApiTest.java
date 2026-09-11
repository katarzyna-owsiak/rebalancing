package org.example.rebalancing.tests;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.example.rebalancing.config.ApiConstants.*;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;

class RebalancingApiTest extends ApiTestBase {

    @Test
    void shouldCorrectlyRebalanceAccountABC() {
        apiMock.stubSuccessfulRebalance();

        String requestBody = loadResourceFromClasspath(ACCOUNT_ABC_REQUEST);

        given()
                .baseUri(getBaseUrl())
                .contentType(CONTENT_TYPE_JSON)
                .body(requestBody)

                .when()
                .post(REBALANCE_ENDPOINT)

                .then()
                .statusCode(HTTP_OK)

                .body(JSON_PATH_ACCOUNT_ID, equalTo(ACCOUNT_ID_ABC))

                .body(JSON_PATH_REBALANCE_SIZE, equalTo(EXPECTED_SECURITIES_COUNT))

                .body(String.format(JSON_PATH_REBALANCE_ACTIONS, SECURITY_IBM), equalTo(ACTION_BUY))
                .body(String.format(JSON_PATH_REBALANCE_SHARES, SECURITY_IBM), closeTo(IBM_EXPECTED_SHARES, SHARES_TOLERANCE))

                .body(String.format(JSON_PATH_REBALANCE_ACTIONS, SECURITY_MSFT), equalTo(ACTION_NONE))
                .body(String.format(JSON_PATH_REBALANCE_SHARES, SECURITY_MSFT), equalTo(NO_ACTION_SHARES))

                .body(String.format(JSON_PATH_REBALANCE_ACTIONS, SECURITY_ORCL), equalTo(ACTION_SELL))
                .body(String.format(JSON_PATH_REBALANCE_SHARES, SECURITY_ORCL), closeTo(ORCL_EXPECTED_SHARES, SHARES_TOLERANCE))

                .body(String.format(JSON_PATH_REBALANCE_ACTIONS, SECURITY_AAPL), equalTo(ACTION_NONE))
                .body(String.format(JSON_PATH_REBALANCE_SHARES, SECURITY_AAPL), equalTo(NO_ACTION_SHARES))

                .body(String.format(JSON_PATH_REBALANCE_ACTIONS, SECURITY_HD), equalTo(ACTION_NONE))
                .body(String.format(JSON_PATH_REBALANCE_SHARES, SECURITY_HD), equalTo(NO_ACTION_SHARES));
    }
}
package org.example.rebalancing.config;

public class ApiConstants {

    // ==================== API ENDPOINTS ====================
    public static final String REBALANCE_ENDPOINT = "/rebalance";

    // ==================== HTTP CONFIGURATION ====================
    public static final String BASE_URL = "http://localhost:8080";
    public static final String HOST = "localhost";
    public static final int WIREMOCK_PORT = 8080;
    public static final String CONTENT_TYPE_JSON = "application/json";

    // ==================== TIMEOUT CONFIGURATION ====================
    public static final int SOCKET_TIMEOUT_MS = 5000;
    public static final int CONNECTION_TIMEOUT_MS = 5000;

    // ==================== TEST DATA ====================
    public static final String ACCOUNT_ABC_REQUEST = "account-abc.json";
    public static final String ACCOUNT_ABC_RESPONSE = "account-abc-response.json";

    // ==================== ACCOUNT DATA ====================
    public static final String ACCOUNT_ID_ABC = "ABC";
    public static final int TOTAL_ASSETS = 100000;
    public static final int VESTED_PERCENTAGE = 100;
    public static final int EXPECTED_SECURITIES_COUNT = 5;

    // ==================== SECURITY DATA ====================
    public static final String SECURITY_IBM = "IBM";
    public static final String SECURITY_MSFT = "MSFT";
    public static final String SECURITY_ORCL = "ORCL";
    public static final String SECURITY_AAPL = "AAPL";
    public static final String SECURITY_HD = "HD";

    // ==================== REBALANCE ACTIONS ====================
    public static final String ACTION_BUY = "BUY";
    public static final String ACTION_SELL = "SELL";
    public static final String ACTION_NONE = "NONE";

    // ==================== SHARE QUANTITIES ====================
    public static final double IBM_EXPECTED_SHARES = 66.6667d;
    public static final double ORCL_EXPECTED_SHARES = 45.4545d;
    public static final double NO_ACTION_SHARES = 0;

    // ==================== FLOATING-POINT TOLERANCE ====================
    public static final double SHARES_TOLERANCE = 0.0001d;

    // ==================== HTTP STATUS CODES ====================
    public static final int HTTP_OK = 200;
    public static final int HTTP_BAD_REQUEST = 400;
    public static final int HTTP_NOT_FOUND = 404;
    public static final int HTTP_SERVER_ERROR = 500;

    // ==================== JSON PATH EXPRESSIONS ====================
    public static final String JSON_PATH_ACCOUNT_ID = "accountId";
    public static final String JSON_PATH_REBALANCE_SIZE = "rebalance.size()";
    public static final String JSON_PATH_REBALANCE_ACTIONS = "rebalance.find { it.symbol == '%s' }.action";
    public static final String JSON_PATH_REBALANCE_SHARES = "rebalance.find { it.symbol == '%s' }.shares";

    private ApiConstants() {
        throw new AssertionError("Cannot instantiate constants class");
    }
}

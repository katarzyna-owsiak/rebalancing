# Rebalancing API – QA Automation Tests



## 📌 Project Overview

The purpose of this project is to demonstrate an independent QA automation approach for a portfolio rebalancing API.

The test suite:

- sends HTTP requests using REST Assured,
- uses WireMock to simulate the API,
- validates HTTP status codes,
- validates the response structure,
- validates returned account information,
- validates rebalancing actions (`BUY`, `SELL`, `NONE`),
- validates calculated share quantities,
- keeps test data separate from test logic,
- does not depend on production application classes or implementation details.

The project also includes a separate set of manual test cases covering
edge cases, boundary conditions, calculation precision and additional
business scenarios that are not currently part of the automated suite.

Manual test cases are available in
[`test-cases/manual-test-cases.md`](test-cases/manual-test-cases.md).

---

# ▶️ Running the Tests

Make sure **Java 17** and **Maven** are installed.

Run:
```bash
mvn test
```

---

# Responsibilities

| Component                     | Responsibility |
|:------------------------------| :--- |
| **WireMockConfig**            | Starts and stops the WireMock server |
| **RebalancingApiMock**        | Defines the mocked API behaviour |
| **account-abc.json**          | Contains request test data |
| **account-abc-response.json** | Contains the mocked API response |
| **RebalancingApiTest**        | Sends requests and performs assertions |
| **pom.xml**                   | Defines project dependencies and build configuration |
| **manual-test-cases.md**      | Manual test scenarios and exploratory test coverage |

---

# 🧪 Test Scenario

The current test uses account ABC with the following portfolio:

| Security | Target | Current | Unit Price | Expected Action |
| :--- | :---: | :---: | :---: | :---: |
| **IBM** | 20% | 10% | 150 | **BUY** |
| **MSFT** | 20% | 20% | 90 | **NONE** |
| **ORCL** | 20% | 30% | 220 | **SELL** |
| **AAPL** | 20% | 20% | 450 | **NONE** |
| **HD** | 20% | 20% | 70 | **NONE** |

The portfolio therefore contains:
* **one underweight security** → `BUY`,
* **one overweight security** → `SELL`,
* **three securities already at their target allocation** → `NONE`.

---

# 📐 Business Rules & Assumptions

The following assumptions are made based on the requirements available for this exercise.

### 1. Target allocation
Each security has a `targetPercentage`.  
For account ABC:
* IBM → 20%
* MSFT → 20%
* ORCL → 20%
* AAPL → 20%
* HD → 20%

The total target allocation is therefore:
* 20% × 5 = 100%

### 2. BUY rule
A security is considered underweight when: **`currentPercentage < targetPercentage`**

The expected action is: **`BUY`**

**Example:**

**IBM**
* Target: 20% 
* Current: 10%
* 10% < 20% → BUY

### 3. SELL rule
A security is considered overweight when:
**`currentPercentage > targetPercentage`**

The expected action is: **`SELL`**

**Example:**
**ORCL**
* Target: 20%
* Current: 30%
* 30% > 20 → SELL

### 4. NONE rule
When:
**`currentPercentage == targetPercentage`**

No rebalancing is required.  
The expected action is: **`NONE`**

**Example:**
**MSFT**
* Target: 20%
* Current: 20%
* → NONE

### 5. Fractional shares
The response contains fractional share quantities.  
For example:

```json
{
  "symbol": "IBM",
  "action": "BUY",
  "shares": 66.6667
}
```

The test therefore uses a numerical tolerance instead of requiring exact floating-point equality.

**Example:**
```groovy
closeTo(66.6667, 0.0001)
```
This avoids false negatives caused by floating-point precision.

### 6. Fully vested account
The test data assumes:
**`vestedPercentage = 100`**

Therefore, the entire account value is considered available for rebalancing.

### 7. Account value
The test uses:
**`totalAssets = 100000`**

This value is assumed to represent the total value of the account used for the rebalancing calculation.

### 8. Bulk rebalancing request
The API is assumed to accept the entire account portfolio in a single request.  
The test therefore sends:
`POST /rebalance` with the complete account and its securities in the request body.

### 9. Successful response
For a valid request, the API is assumed to return:
```http
HTTP/1.1 200 OK
Content-Type: application/json
```
with the rebalancing instructions in the response body.

### 10. Security order
The test does not assume that securities must appear in any particular order in the response.  
Assertions locate securities by their symbol, for example:

```groovy
rebalance.find { it.symbol == 'IBM' }
```
This makes the test less coupled to the ordering of response elements.

---

# 🧮 Expected Calculation

For the purpose of this test scenario, the expected number of shares is derived from the target allocation and account value.

#### For an underweight security:
**`Target value = total assets x target percentage`**

* **For IBM:**
    * Target value: $100,000 x 20% = 20,000$
    * Current IBM value: $100,000 x 10% = 10,000$
    * Required additional value: $20,000 - 10,000 = 10,000$
    * With a unit price of $150$, the expected number of shares is: 10,000 / 150 = 66.6667$$
    * **Therefore:** `IBM` → `BUY` → **66.6667 shares**

* **For ORCL:**
    * Current value: $100,000 x 30% = 30,000$
    * Target value: $100,000 x 20% = 20,000$
    * Excess value: $30,000 - 20,000 = 10,000$
    * Shares:
      10,000 / 220 = 45.4545
    * **Therefore:** `ORCL` → `SELL` → **45.4545 shares**
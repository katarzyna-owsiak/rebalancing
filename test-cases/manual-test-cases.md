# 🧪 Manual Test Cases (MT-01 – MT-07)

This document details the manual test scenarios for validating the portfolio rebalancing API.

---

## 📋 Test Cases Summary

| ID | Title | Priority | Type |
| :--- | :--- | :---: | :--- |
| **MT-01** | Zero Account Assets (`totalAssets = 0`) | Medium | Edge case / Boundary |
| **MT-02** | Security With 0% Current Allocation | Medium | Edge case |
| **MT-03** | Share Quantity Rounding | High | Calculation / Precision |
| **MT-04** | Partially Vested Account | High | Business rule |
| **MT-05** | Very Large Account Value | Medium | Boundary / Calculation |
| **MT-06** | Decimal Unit Price | High | Calculation / Precision |
| **MT-07** | Simultaneous BUY and SELL | High | Business scenario |

---

### MT-01 — Zero Account Assets (`totalAssets = 0`)

* **Priority:** `Medium`
* **Test Data:**
    * `totalAssets` = `0`

#### Steps
1. Prepare a valid rebalance payload for an account.
2. Set `totalAssets` to `0`.
3. Send the HTTP `POST /rebalance` request.
4. Observe the API response status code and body.

#### Expected Result
* The API handles an account with zero assets gracefully according to the defined business requirements (e.g., returns `200 OK` with `0` shares or `400 Bad Request` depending on specification).
* No division-by-zero errors, invalid share calculations (`NaN`, `Infinity`), or unhandled exceptions occur.

---

### MT-02 — Security With 0% Current Allocation

* **Priority:** `Medium`

* **Test Data:**
    * Security `currentPercentage` = `0%`
    * Security `targetPercentage` = `20%`

#### Steps
1. Prepare a valid rebalance request payload.
2. Include a security with `currentPercentage = 0%` and a non-zero `targetPercentage`.
3. Send the `POST /rebalance` request.
4. Observe the returned rebalance result for the 0% allocated security.

#### Expected Result
* The security is identified as underweight and assigned a `BUY` action.
* The API returns the appropriate share quantity required to reach the target allocation without calculation errors.

---

### MT-03 — Share Quantity Rounding

* **Priority:** `High`
* **Test Data:**
    * Unit prices and percentages that produce a repeating decimal (e.g., target amount = \$10,000, `unitPrice` = \$150 → 66.6666... shares)

#### Steps
1. Prepare a portfolio payload where the calculated number of shares results in a repeating decimal.
2. Send the `POST /rebalance` request.
3. Inspect the returned `shares` value for the affected security.

#### Expected Result
* The number of shares is rounded consistently according to the API's defined rounding rules (e.g., 4 decimal places).
* The result contains no floating-point arithmetic errors or unexpected precision drift.

---

### MT-04 — Partially Vested Account

* **Priority:** `High`
* **Test Data:**
    * `vestedPercentage` = `85%`
    * `totalAssets` = `$100,000` (Effective assets for rebalancing = `$85,000`)

#### Steps
1. Prepare an otherwise valid account payload.
2. Set `vestedPercentage` to `85`.
3. Send the `POST /rebalance` request.
4. Inspect the returned rebalance instructions and calculated share quantities.

#### Expected Result
* Rebalancing calculations consider **only** the vested portion of the account balance ($85\%$ of total assets).
* Unvested assets are excluded from the rebalancing calculations according to business rules.

---

### MT-05 — Very Large Account Value

* **Priority:** `Medium`
* **Test Data:**
    * `totalAssets` = `9,999,999,999,999.99`

#### Steps
1. Prepare an account payload with a very large `totalAssets` value.
2. Send the valid `POST /rebalance` request.
3. Inspect the API response status and calculate expected share quantities.

#### Expected Result
* The API processes the request successfully without numeric overflow, loss of precision, or string formatting issues.
* Share quantities are accurately calculated for large asset values.

---

### MT-06 — Decimal Unit Price

* **Priority:** `High`
* **Test Data:**
    * `unitPrice` = `123.45`

#### Steps
1. Prepare a valid portfolio payload containing a security with a non-integer `unitPrice` (e.g., `123.45`).
2. Send the `POST /rebalance` request.
3. Inspect the calculated `shares` value for that security.

#### Expected Result
* The share quantity should be calculated correctly using the decimal
  unit price and follow the defined rounding rules.

---

### MT-07 — Simultaneous BUY and SELL

* **Priority:** `High`
* **Test Data:**
    * Security A: `currentPercentage < targetPercentage` (Underweight)
    * Security B: `currentPercentage > targetPercentage` (Overweight)
    * Security C: `currentPercentage == targetPercentage` (Balanced)

#### Steps
1. Prepare a portfolio payload containing:
    * At least one security below target allocation.
    * At least one security above target allocation.
    * At least one security at target allocation.
2. Send the `POST /rebalance` request.
3. Inspect all returned rebalance instructions.

#### Expected Result
* The API response correctly categorizes all actions:
    * Underweight security $\rightarrow$ **`BUY`**
    * Overweight security $\rightarrow$ **`SELL`**
    * Balanced security $\rightarrow$ **`NONE`**
* Share quantities for both `BUY` and `SELL` instructions are calculated accurately.
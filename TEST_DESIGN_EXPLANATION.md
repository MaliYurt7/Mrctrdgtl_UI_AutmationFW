# Test Design Explanation

## UI (SauceDemo)

### Approach

All UI scenarios target [SauceDemo](https://www.saucedemo.com/) and are built around its documented set of test user accounts (`standard_user`, `locked_out_user`, `problem_user`, `performance_glitch_user`, `error_user`, `visual_user`), each of which is designed by SauceDemo to trigger a specific behaviour (successful login, blocked login, visual/data bugs, slow rendering, partial functional errors). Test design is driven by exercising each account against the relevant part of the purchase journey (login → product browsing → cart → checkout → confirmation), rather than testing every step with every account.

Test data (username, password, expected result) is kept in `LoginCredentials_TestData.csv` and referenced from the feature files by `TestCaseID`, so scenarios describe *behaviour* and the data lives in one place.

### Rationale for implemented scenarios

| Scenario | Rationale |
|---|---|
| **`Login.feature` – verify that users can access the main page** (data-driven via Scenario Outline/Examples across all 6 SauceDemo user types + an empty-credentials case) | Covers both positive (each valid user type reaches the Products page) and negative (`locked_out_user` blocked with an error message, empty username/password rejected) login outcomes in one reusable flow. Using all documented user types ensures each account's distinct behaviour is explicitly asserted rather than assumed. |
| **`HighestPriceProduct.feature` – add the highest priced product and complete checkout** (`standard_user`, tag `@highestPricedPrdct`) | Directly implements the work-sample requirement: select the most expensive product **without the "Sort By" option**. The scenario then follows the product through the cart (exactly 1 item, named `Sauce Labs Fleece Jacket`), checkout details, the order overview (`Item total: $49.99`), the confirmation message and the return to Products. Following the product to the end proves that the item chosen by the selection logic is the item that is actually purchased, not just that a button was clicked. |
| **`HighestPriceProduct.feature` – other products are not in the cart** (`standard_user`, tag `@otherProdouctNotSeen`) | Negative companion to the scenario above. After adding the highest priced product, a data table lists the five other products and asserts none of them is in the cart. It guards against the typical failure of this kind of automation: the click landing on the wrong product, or an extra product being added. |
| **`Product.feature` – different product pictures (`problem_user`)** | `problem_user` is SauceDemo's documented "broken UI/data" account. This scenario checks for the known duplicate/broken product image behaviour, acting as a regression check that this documented defect state is still reproducible and detected — useful for confirming the test suite can catch UI-level bugs, not just functional ones. |
| **`Product.feature` – add to cart without restriction (`error_user`)** | `error_user` is documented to behave inconsistently on certain actions. Verifying that "Add to cart" still succeeds (button flips to "Remove") for this account checks that partial/expected error behaviour doesn't block the core add-to-cart action, distinguishing genuine functional breakage from the account's known quirks. |
| **`ProductCheckOutJourney.feature` – full checkout journey (`standard_user`)** | End-to-end smoke test (`@smokeTest`) covering the highest business-value path: login → add multiple products → cart → checkout details → order overview (with price total) → order confirmation → return to Products page. `standard_user` was chosen for this scenario specifically because it has no known account-level quirks, so any failure here reliably indicates a real regression in the checkout flow itself rather than account-specific behaviour. |

**Prioritisation:** the full checkout journey, the highest priced product flow and the core login flow are **High** priority (primary business flows and the explicit requirement of the exercise); the "other products not in the cart" check is **High/Medium** (cheap to run and protects the selection logic); the `problem_user`/`error_user` checks are **Medium** priority (account-specific edge behaviour, valuable but secondary to core flow correctness).

### Design note: selecting the highest priced product without "Sort By"

The requirement forbids the *Sort By* dropdown, so the price comparison is done in test code.

- **Implemented approach:** read every `.inventory_item_price` element, parse the text (`$49.99` → `49.99`) to a number, keep the element with the greatest value, then locate the *Add to cart* button relative to that price element (`./following-sibling::button`).
- **Why this approach:**
  - *Dynamic*: still correct if products or prices change; nothing is hard-coded in the selection logic.
  - *Safe pairing*: price and button are found through the same DOM structure, so the click can't land on a different product.
  - *Numeric comparison*: prices are compared as numbers; comparing the raw strings would rank `$9.99` above `$49.99`.

### Design note: Scenario Outline + CSV test data

All UI scenarios use `Scenario Outline` with a `TestCaseID` and the CSV file, even where only one row exists (e.g. `HighestPriceProduct.feature`).

- **Why:** one consistent login mechanism across the suite, credentials kept out of the feature files, and new users/cases added by editing the CSV and the `Examples` table.
- **Trade-off accepted:** a little indirection for single-row cases, in exchange for consistency and easy extension to other user types.

### Design note: other user types commented out in the checkout journey

`ProductCheckOutJourney.feature` lists the other five user types as commented-out `Examples` rows rather than active cases. This was a deliberate scope decision: running the full checkout flow against accounts with known UI/data quirks (`problem_user`, `visual_user`) would require scenario-specific assertions to distinguish "expected known quirk" from "genuine failure," which was out of scope for this exercise. They're left commented to signal intended future coverage rather than an oversight.

### Known limitations and future improvements

| Area | Observation | Suggested improvement |
|---|---|---|
| Waits | `LoginFlow` uses fixed waits (`BrowserUtils.waitFor`) around login | Replace with explicit waits (`WebDriverWait` / expected conditions) to make runs faster and more stable |
| Assertions | Flow classes mix assertion libraries (JUnit 4 and TestNG imports in `ProductsFlow`) | Standardise on JUnit 4 (the runner in use) and keep the dependency list consistent with `pom.xml` |
| Scenario naming | The first scenario in `HighestPriceProduct.feature` reuses the name "verify that users can access the main page" from `Login.feature` | Rename it to describe the real behaviour, e.g. "Highest priced product can be purchased end to end" |
| Step wording | Step `Then the total price should  Item total: $49.99` is not a clean sentence | Rephrase, e.g. `Then the item total should be "$49.99"` |
| Selection logic | Ties on the highest price pick the first product | Decide the expected behaviour for ties and cover it if the catalogue ever changes |

### Further UI scenarios (outlined, not automated)

| Scenario | Type | Approach | Rationale | Priority |
|---|---|---|---|---|
| Checkout with one or more required fields left empty (First Name / Last Name / Zip) | Negative | Submit the Checkout: Your Information form with a field blank; assert the SauceDemo inline error message for that field | Validates client-side required-field enforcement on the checkout form, a common real-world failure point | High |
| Highest priced product selected for every user type (`problem_user`, `performance_glitch_user`, `error_user`, `visual_user`) | Positive | Re-use the `@highestPricedPrdct` scenario with additional `Examples` rows; add user-specific assertions where known quirks apply | Confirms the selection logic is not account-dependent | Medium |
| Highest priced product is added once only (double click on *Add to cart*) | Negative | Click the button twice; assert the badge stays at 1 and the button shows *Remove* | Protects against duplicate additions and state flipping | Medium |
| Attempt to reach Checkout: Overview / Complete pages directly via URL without completing prior steps | Negative | Navigate directly to a later checkout step URL while cart/session state is incomplete; assert user is redirected or blocked | Tests the app doesn't allow skipping required steps in an unexpected state | Medium |
| Remove a product from "Your Cart" page before checkout | Positive | Click "Remove" on a cart item; assert cart badge count and item list update accordingly | Common real user action; verifies cart state consistency outside the happy-path add flow | Medium |
| "Cancel" button on Checkout: Your Information and Checkout: Overview pages | Positive | Click "Cancel"; assert user is returned to Cart / Products page respectively without completing the order | Verifies users can safely back out of checkout without unintended side effects (e.g. order still placed) | Medium |
| Logout via the hamburger menu | Positive | Click menu → Logout; assert redirect to the login page and that session/cart state doesn't persist | Verifies session termination behaves correctly, relevant for security/data-isolation between test runs | Medium |
| Access the inventory page without logging in | Negative | Open `/inventory.html` directly; assert the "You can only access '/inventory.html' when you are logged in" error | Confirms protected pages cannot be reached without a session | Medium |
| Sort products (Name A–Z/Z–A, Price low–high/high–low) | Positive | Select each sort option from the dropdown; assert product order matches expected sort | Commonly used feature not currently covered; also gives an independent cross-check for the "highest price" logic (first item after *Price high to low* must equal the product chosen by the loop) | Low |
| `performance_glitch_user` full journey with explicit wait/timeout assertions | Positive | Run the core login/checkout flow with this account; assert functional correctness despite the deliberately slower page loads | Ensures test automation and the app itself remain functionally correct under degraded performance, not just under ideal timing | Low |
| Add an out-of-stock/removed product reference (e.g. stale product ID) to cart | Negative | Attempt to reference a product that no longer exists in the current inventory listing; assert graceful handling | Simulates an unexpected/stale state (e.g. session or catalog change mid-journey) | Low |
| `visual_user` visual regression check | Positive | Capture and compare screenshots of key pages (Products, Cart) for `visual_user` against a baseline | `visual_user` exists specifically to test visual regressions; not automated here as it requires a visual-diff tool not currently in the framework | Low |

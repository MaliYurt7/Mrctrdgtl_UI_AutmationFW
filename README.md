# SauceDemo UI Automation Framework

UI test automation for [SauceDemo](https://www.saucedemo.com/) built with **Java 21, Selenium 4, Cucumber (BDD) and JUnit 4**.
Tests are written in Gherkin, data-driven from a CSV file, and reported with **Allure** and **cucumber-reporting**.

> Test strategy, scenario rationale and prioritisation: see [TEST_DESIGN_EXPLANATION.md](TEST_DESIGN_EXPLANATION.md).

---

## Tech stack

| Area | Tool / Version |
|---|---|
| Language | Java 21 |
| Build | Maven |
| Browser automation | Selenium 4.46.0 + WebDriverManager 6.3.3 |
| BDD | Cucumber 7.18.0 (`cucumber-java`, `cucumber-junit`) |
| Test runner | JUnit 4.13.2 |
| Reporting | Allure 2.32.0, cucumber-reporting 5.10.1 |
| Test data / utilities | OpenCSV, Apache POI, org.json, json-simple, Lombok, commons-io |

---

## Prerequisites

- JDK 21
- Maven 3.9+
- Google Chrome (default browser, driver is resolved automatically by WebDriverManager)
- Internet access (tests run against the public SauceDemo site)
- *(Optional)* [Allure CLI](https://allurereport.org/docs/install/) for viewing Allure reports

---

## Project structure

```
src
└── test
    ├── java/com/sauceUI
    │   ├── runners
    │   │   ├── CukesRunner.java          # main runner (tag-filtered, writes rerun.txt)
    │   │   └── FailedTestRunner.java     # re-runs only the scenarios that failed
    │   ├── step_def
    │   │   ├── BasePage.java             # PageFactory init + shared navigation check
    │   │   ├── LoginPage/                # LoginPage (locators), LoginFlow (logic), LoginStepDef (steps)
    │   │   ├── ProductsPage/             # ProductsPage, ProductsFlow, ProductsStepDef
    │   │   ├── YourCartPage/             # cart page objects / flow
    │   │   ├── Checkout_YourInformation/ # checkout step 1
    │   │   ├── Checkout_Overview/        # checkout step 2
    │   │   └── Checkout_Complete/        # order confirmation
    │   └── utilities                     # Driver, BrowserUtils, GenericUtils, ReportGenerator, Hooks
    └── resources
        ├── *.feature                     # Login, Products, HighestPriceProduct, checkout journey
        └── ui_TestData
            └── LoginCredentials_TestData.csv
configuration.properties                  # browser, environment, test data path
configuration.json                        # environment name, URL, standard user/password
```

Each page area follows the same three-layer pattern:

| Layer | Responsibility |
|---|---|
| `*Page` | Locators only (`@FindBy`, PageFactory) |
| `*Flow` | Page logic and assertions |
| `*StepDef` | Thin Cucumber glue that delegates to the flow |

---

## Configuration

**`configuration.properties`**

```properties
browser=chrome
testEnvironment=UI
loginCredentials_CSVFilePath:src/test/resources/ui_TestData/LoginCredentials_TestData.csv
```

**`configuration.json`**

```json
[{ "environment": { "environmentName": "UI",
                    "standardUsername": "standard_user",
                    "commanPassword": "secret_sauce",
                    "url": "https://www.saucedemo.com/" } }]
```

### Test data (CSV)

Login scenarios are data-driven. A scenario passes a `TestCaseID` and the framework reads that row from `LoginCredentials_TestData.csv`:

| TestCaseID | username | expectedResult |
|---|---|---|
| SuccessAccess_Standard | standard_user | Products |
| InvalidAccess_LockedOut | locked_out_user | Epic sadface: Sorry, this user has been locked out. |
| SuccessAccess_Problem | problem_user | Products |
| SuccessAccess_Performance | performance_glitch_user | Products |
| SuccessAccess_Error | error_user | Products |
| SuccessAccess_Visual | visual_user | Products |
| EmptyValue | *(empty)* | Epic sadface: Username is required |

To add a new login case, add a row to the CSV and a matching `TestCaseID` to the `Examples` table. No Java change is needed for the standard cases.

---

## Feature files

| Feature | What it covers |
|---|---|
| `Login.feature` | Login for all 6 SauceDemo user types plus empty credentials (positive and negative) |
| `HighestPriceProduct.feature` | Finds and adds the most expensive product **without using the "Sort By" option**, then completes checkout. A second scenario checks that no other product ends up in the cart |

### Tags

| Tag | Meaning |
|---|---|
| `@smoke` | Smoke suite. This is the default filter in `CukesRunner` |
| `@LoginPage`, `@AccessMainPage` | Login scenarios |
| `@cart` | Cart related scenarios |
| `@highestPricedPrdct` | Highest priced product end-to-end scenario |
| `@otherProdouctNotSeen` | Negative check: other products are not in the cart |

---

## Running the tests

```bash
# Run the default suite (CukesRunner, tag @smoke)
mvn clean test

# Run a single tag (the system property overrides the tag in CukesRunner)
mvn clean test -Dcucumber.filter.tags="@highestPricedPrdct"
mvn clean test -Dcucumber.filter.tags="@AccessMainPage"

# Combine tags
mvn clean test -Dcucumber.filter.tags="@smoke and not @AccessMainPage"

# Re-run only the failed scenarios from the last run
mvn test -Dtest=FailedTestRunner
```

Surefire picks up every class ending in `Runner` (`**/*Runner.java`) and starts the AspectJ weaver agent that Allure needs.

### How the failed-test re-run works

`CukesRunner` writes every failed scenario to `target/rerun.txt` (`rerun:` plugin). `FailedTestRunner` reads that file (`features = "@target/rerun.txt"`) and runs only those scenarios.

---

## Reports

| Report | Output | How to open |
|---|---|---|
| Cucumber JSON | `target/cucumber-reports/cucumber.json` | Raw results |
| cucumber-reporting HTML | Generated by `ReportGenerator.generateReport()` after the run (`@AfterClass` in `CukesRunner`) | Open the generated HTML under `target/` |
| Allure | `target/allure-results` | `mvn allure:serve` or `mvn allure:report` |

---

## Key design points

- **Page Object Model with PageFactory**: locators live in `*Page` classes, all pages extend `BasePage`.
- **Flow layer**: business actions and assertions are kept out of the step definitions.
- **Data-driven login**: credentials and expected results come from the CSV, not from the feature file.
- **Highest priced product without sorting**: all `.inventory_price` elements are read, prices are parsed to numbers and the maximum is chosen. The *Add to cart* button is then located relative to that price element, so the name, price and button always belong to the same product.
- **Re-runnable failures** through `rerun.txt` and `FailedTestRunner`.

---

## Troubleshooting

| Problem | Fix |
|---|---|
| `ChromeDriver` / browser version mismatch | Update Chrome or let WebDriverManager download a matching driver (needs internet) |
| Allure report is empty | Make sure the tests ran through Maven so the AspectJ agent in `maven-surefire-plugin` is active |
| `NullPointerException` on test data | Check `loginCredentials_CSVFilePath` in `configuration.properties` and that the `TestCaseID` exists in the CSV |
| Nothing runs | `CukesRunner` filters on `@smoke`. Add the tag to your scenario or pass `-Dcucumber.filter.tags` |

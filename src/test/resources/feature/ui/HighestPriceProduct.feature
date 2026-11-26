@cart @smoke
Feature: Add the highest priced product to the cart
  As a user
  I want to find the most expensive product on the inventory page without using the sort option
  So that I can add it to my cart quickly

  @highestPricedPrdct
  Scenario Outline: verify that users can access the main page
    And retrieve "<TestCaseID>" from LoginCredentials testdata file
    And user types "username" from "<TestCaseID>" in "Username" input box
    And user types "password" from "<TestCaseID>" in "Password" input box
    And user clicks the "Login" button
    Then page should navigate to "Products" page
    When users adds the highest priced product to the cart
    Then user should see number 1 at the cart
    Then click the shopping cart link
    Then page should navigate to "Your Cart" page
    Then 1 product(s) should be displayed on the "Your Cart" page
    And user should see the only the highest priced product named "Sauce Labs Fleece Jacket" at the cart
    And user clicks the "Checkout" button
    Then page should navigate to "Checkout: Your Information" page
    Then user enters the following details
      | FirstName      | Micheal |
      | LastName       | Tyson   |
      | Zip/PostalCode | ST1 4EX |
    And user clicks the "Continue" button
    Then page should navigate to "Checkout: Overview" page
    Then 1 products should be displayed on the "Checkout: Overview" page
    Then the total price should  Item total: $49.99
    And user clicks the "Finish" button
    Then page should navigate to "Checkout: Complete!" page
    Then user should see the "Thank you for your order!" message
    Then user clicks the "Back Home" button
    Then page should navigate to "Products" page
    Then user should see 6 "Add to cart" button

    Examples:
      | TestCaseID             |
      | SuccessAccess_Standard |


  @otherProdouctNotSeen
  Scenario Outline: Other products are not in the cart when only the highest priced product is added
    And retrieve "<TestCaseID>" from LoginCredentials testdata file
    And user types "username" from "<TestCaseID>" in "Username" input box
    And user types "password" from "<TestCaseID>" in "Password" input box
    And user clicks the "Login" button
    Then page should navigate to "Products" page
    When users adds the highest priced product to the cart
    Then user should see number 1 at the cart
    Then click the shopping cart link
    Then page should navigate to "Your Cart" page
    Then 1 product(s) should be displayed on the "Your Cart" page
    Then the cart should not contain the following products
      | Sauce Labs Backpack               |
      | Sauce Labs Bolt T-Shirt           |
      | Sauce Labs Onesie                 |
      | Sauce Labs Bike Light             |
      | Test.allTheThings() T-Shirt (Red) |



    Examples:
      | TestCaseID             |
      | SuccessAccess_Standard |
package com.sauceUI.step_def.YourCartPage;

import io.cucumber.java.en.*;

import java.util.List;

public class YourCartStepDef {

    YourCartFlow yourCartFlow = new YourCartFlow();

    @Then("{int} product\\(s) should be displayed on the {string} page")
    public void product_s_should_be_displayed_on_the_page(int expectedProductNumber, String pageName) {

        yourCartFlow.getNumberOfProduct(expectedProductNumber);
    }

    @Then("user should see the only the highest priced product named {string} at the cart")
    public void user_should_see_the_only_the_highest_priced_product_named_at_the_cart(String expectedProductNames) {
        yourCartFlow.getProductNames(expectedProductNames);
    }

    @Then("the cart should not contain the following products")
    public void the_cart_should_not_contain_the_following_products(List<String> productsNames) {
        yourCartFlow.getNotProductNames(productsNames);

    }
}

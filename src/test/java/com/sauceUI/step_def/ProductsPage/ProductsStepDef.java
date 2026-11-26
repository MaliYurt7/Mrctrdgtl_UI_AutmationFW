package com.sauceUI.step_def.ProductsPage;

import com.sauceUI.utilities.GenericUtils;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;

import java.util.List;
import java.util.Map;

public class ProductsStepDef {


    GenericUtils utils = new GenericUtils();
    ProductsFlow productsFlow = new ProductsFlow();
    public double totalPrice;




    @Then("click the shopping cart link")
    public void click_the_shopping_cart_link() {
        productsFlow.clickShoppingCartLink();
    }

    @Then("page should navigate to {string} page")
    public void page_should_navigate_to_page(String moduleName) {
        productsFlow.validateThePageName(moduleName);
    }

    @When("users adds the highest priced product to the cart")
    public void users_adds_the_highest_priced_product_to_the_cart() {

        productsFlow.addHighestPricedProductToCart();

    }


    @Then("user should see number {int} at the cart")
    public void user_should_see_number_at_the_cart(int productNumber) {
        productsFlow.validateTheSelectedProductNumber(productNumber);
    }


    @Then("user should see {int} {string} button")
    public void user_should_see_button(int AddToCardBtnNumber, String addToCart) {
        productsFlow.validateTheAddToCardBtnNumber(AddToCardBtnNumber);
    }

}

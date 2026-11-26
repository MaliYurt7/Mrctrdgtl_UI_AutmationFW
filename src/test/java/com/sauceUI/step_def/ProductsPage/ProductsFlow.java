package com.sauceUI.step_def.ProductsPage;

import com.sauceUI.step_def.BasePage;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

import static com.sauceUI.utilities.BrowserUtils.*;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;

public class ProductsFlow extends BasePage {

    ProductsPage productsPage = new ProductsPage();

    public void addTheProductInTheCart(List<Map<String, String>> items) {
        double totalPrice = 0.0;

        for (Map<String, String> item : items) {
            String product = item.get("productName");
            productsPage.getProduct(product).click();

            String priceRaw = item.get("actualPrice");
            waitForVisibility(productsPage.getProductPrice(product), 5);
            Assert.assertEquals(productsPage.getProductPrice(product).getText(), priceRaw);

            String cleaned = priceRaw.replace("$", "").trim();
            double price = Double.parseDouble(cleaned);
            double lineTotal = price;
            totalPrice += lineTotal;

        }

    }

    public void getNumberOfTheProductsOnTheCartImage(int expectedProductNumber) {
        Assert.assertEquals(Integer.parseInt(productsPage.getShoppingCardLink.getText()), expectedProductNumber);
    }

    public void clickShoppingCartLink() {
        waitForPresenceOfElement(By.cssSelector("a.shopping_cart_link"), 2);
        productsPage.getShoppingCardLink.click();
    }

    public void validateThePageName(String moduleName) {
        waitForPresenceOfElement(By.xpath("//span[text()='" + moduleName + "']"), 10);
        Assert.assertTrue(moduleName.contains(pageNavigation(moduleName)));
    }

    public void validateTheAddToCardBtnNumber(int AddToCardBtnNumber) {
        Assert.assertEquals(productsPage.getAddToCard.size(), AddToCardBtnNumber);
    }


    public void getProducpictures() {
        int numberOfSamepictures = productsPage.getImgProductPictures.size();
        Assert.assertTrue(numberOfSamepictures == 1);
    }


    public void tryToAddTheProductInTheCart(List<Map<String, String>> items) {


        for (Map<String, String> item : items) {
            String product = item.get("productName");
            String getAddToCart = productsPage.getProduct(product).getText();
            productsPage.getProduct(product).click();
            String idValue = productsPage.getButtonIdAttributeCheck(product).getAttribute("id");
            Assert.assertTrue(idValue.contains("remove"));

        }
    }

    public void addHighestPricedProductToCart() {
        List<WebElement> products = productsPage.getProductsLists;


        assertFalse("Sayfada ürün bulunamadı.", products.isEmpty());

        WebElement maxPriceElement = productsPage.getAllProductsPrice.get(0);


        for (WebElement priceElement : productsPage.getAllProductsPrice) {
            if (parsePrice(priceElement) > parsePrice(maxPriceElement)) {
                maxPriceElement = priceElement;
            }
        }
        productsPage.getAddToCartButton(maxPriceElement).click();

    }

    public void validateTheSelectedProductNumber(int productNumber) {
        assertEquals(productNumber, Integer.parseInt(productsPage.getNumberFromShoppingCartBadge.getText()));
    }
}
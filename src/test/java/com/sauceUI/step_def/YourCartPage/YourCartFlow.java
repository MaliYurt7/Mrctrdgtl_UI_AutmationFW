package com.sauceUI.step_def.YourCartPage;

import org.junit.Assert;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class YourCartFlow {

    YourCartPage yourCartPage =new YourCartPage();

    public void getNumberOfProduct(int expectedProductNumber){
        assertEquals(yourCartPage.getProductNames.size(),expectedProductNumber);
    }


    public void getProductNames(String expectedProductNames){
        for (WebElement getProductName : yourCartPage.getProductNames) {
            assertEquals(expectedProductNames,getProductName.getText());

        }

    }

    public void getNotProductNames(List<String> expectedProductNames){
        List<String> actualProductNames = new ArrayList<>();

        for (WebElement getProductName : yourCartPage.getProductNames) {
            actualProductNames.add(getProductName.getText());
        }
        Assert.assertTrue(
                "Unexpected products found in cart!",
                Collections.disjoint(actualProductNames, expectedProductNames)
        );
    }

}

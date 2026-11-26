package com.sauceUI.step_def.Checkout_Overview;

import org.junit.Assert;

import java.util.Optional;

public class CheckoutOverviewFlow {

    CheckoutOverviewPage checkoutOverviewPage = new CheckoutOverviewPage();

    public void validateItemTotal(Double expectedItemTotalPrice) {

        String getItemTotalText = checkoutOverviewPage.getItemTotalPrice.getText();
        double actualItemTotalPrice = Double.parseDouble(getItemTotalText.split("\\$")[1]);
        Assert.assertEquals(expectedItemTotalPrice, actualItemTotalPrice, 0.01);

    }


}

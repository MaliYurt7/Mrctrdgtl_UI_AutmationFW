package com.sauceUI.step_def.Checkout_Complete;


import org.junit.Assert;

public class CheckoutCompleteFlow {

    CheckoutCompletePage checkoutCompletePage = new CheckoutCompletePage();

    public void validateSuccessMsg(String expectedThankYouMsg) {

        Assert.assertEquals( expectedThankYouMsg,checkoutCompletePage.getThankYouMsg.getText());

    }
}

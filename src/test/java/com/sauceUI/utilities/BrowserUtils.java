package com.sauceUI.utilities;

import org.junit.Assert;
import org.openqa.selenium.Point;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.*;
import java.util.stream.Collectors;

public class BrowserUtils {

    /**
     * Performs a pause
     *
     * @param seconds
     */
    public static void waitFor(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Waits for the provided element to be visible on the page
     *
     * @param element
     * @param timeToWaitInSec
     * @return
     */
    public static WebElement waitForVisibility(WebElement element, int timeToWaitInSec) {
        WebDriverWait wait = new WebDriverWait(Driver.get(), Duration.ofSeconds(timeToWaitInSec));
        return wait.until(ExpectedConditions.visibilityOf(element));
    }


    /**
     * checks that an element is present on the DOM of a page. This does not
     * * necessarily mean that the element is visible.
     *
     * @param by
     * @param time
     */
    public static void waitForPresenceOfElement(By by, long time) {
        new WebDriverWait(Driver.get(), Duration.ofSeconds(time)).until(ExpectedConditions.presenceOfElementLocated(by));
    }


    /**
     * Parses the price text of a WebElement and converts it into a double value.
     * Removes currency symbols, commas, and leading/trailing whitespace before parsing.
     *
     * @param element the WebElement containing the price text
     * @return the parsed price as a double
     */

    public static  double parsePrice(WebElement element) {
        return Double.parseDouble(
                element.getText().replace("$", "").replace(",", "").trim());
    }


}


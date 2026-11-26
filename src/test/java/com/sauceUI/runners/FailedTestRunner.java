package com.sauceUI.runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;


@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = {
                "pretty",
                "json:target/cucumber-reports/failed-tests.json",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
        },
        features = "@target/rerun.txt",
        glue = "com/sauceUI"
)


public class FailedTestRunner {
}

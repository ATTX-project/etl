/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uh.attx.etl.unifiedviews.test.stepdefinitions;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java8.En;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author jkesanie
 */
public class GithubSteps implements En {

    private WebDriver driver = null;

    @Before
    public void createDriver() {
      driver = new ChromeDriver();
      
    }

    @After
    public void quitDriver() {
      driver.quit();
    }

    public static WebElement getWebElementById(WebDriver driver, String id) {
        WebElement myDynamicElement = null;
        try {
            myDynamicElement = (new WebDriverWait(driver, 10))
                    .until(ExpectedConditions.presenceOfElementLocated(By
                            .id(id)));
            return myDynamicElement;
        } catch (TimeoutException ex) {
            return null;
        }
    }    
    
    public static WebElement getWebElementByName(WebDriver driver, String name) {
        WebElement myDynamicElement = null;
        try {
            myDynamicElement = (new WebDriverWait(driver, 10))
                    .until(ExpectedConditions.presenceOfElementLocated(By
                            .name(name)));
            return myDynamicElement;
        } catch (TimeoutException ex) {
            return null;
        }
    }
    public GithubSteps() {
        System.setProperty("webdriver.chrome.driver" ,  "/Users/jkesanie/Applications/chromedriver");

        When("^he logs in$", () -> {
            assertEquals(true, true);
        });

        Given("^he is admin$", () -> {
            // Write code here that turns the phrase above into concrete actions
            assertEquals(true, true);
        });
        
        
    }
}

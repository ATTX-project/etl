/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uh.attx.etl.unifiedviews.test.stepdefinitions;

import cucumber.api.PendingException;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java8.En;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 *
 * @author jkesanie
 */
public class UnifiedViewsSteps implements En {
    private WebDriver driver = null;

    @Before
    public void createDriver() {
  
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
      
    }

    @After
    public void quitDriver() {
      driver.quit();
    }
    public UnifiedViewsSteps() {
        System.setProperty("webdriver.chrome.driver" ,  "/Users/jkesanie/Applications/chromedriver");    
        
        Given("^the user \"([^\"]*)\" has an account$", (String arg1) -> {
            // Write code here that turns the phrase above into concrete actions
            
        });
        
        When("^he logs in$", () -> {
            driver.get("http://localhost:8080/unifiedviews/");            
            WebElement username = driver.findElement(By.cssSelector("input[type='text']"));
            username.sendKeys("Admin");
            WebElement password = driver.findElement(By.cssSelector("input[type='password']"));
            password.sendKeys("test");
            WebElement login = driver.findElement(By.cssSelector("div[role='button']"));
            login.click();
        });
        
        Then("^he should see \"([^\"]*)\"$", (String text) -> {
            WebDriverWait wait = new WebDriverWait(driver,5);
            WebElement e = driver.findElement(By.xpath("//h2[contains(text(),'" + text + "')]"));
            assert e != null;
            
       

        });        
    }
    
}

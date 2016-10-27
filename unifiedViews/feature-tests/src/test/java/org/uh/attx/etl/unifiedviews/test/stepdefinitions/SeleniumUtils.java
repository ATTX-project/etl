/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uh.attx.etl.unifiedviews.test.stepdefinitions;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 *
 * @author jkesanie
 */
public class SeleniumUtils {
    
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
    
    
}

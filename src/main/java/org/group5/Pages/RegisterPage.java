package org.group5.Pages;

import org.group5.BaseClass;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class RegisterPage extends BaseClass {
    public RegisterPage (WebDriver driver) {
        super(driver);
    }

    //Declaring the main elements for the login page
    WebElement emailField = driver.findElement(By.xpath("//*[@id=\"input-email\"]"));
    WebElement SignupBoxTitle = driver.findElement(By.xpath("//*[@id=\"content\"]/h1"));
    WebElement firstName = driver.findElement(By.xpath("//*[@id=\"input-firstname\"]"));
    WebElement lastName = driver.findElement(By.xpath("//*[@id=\"input-lastname\"]"));
    WebElement passwdField = driver.findElement(By.xpath("//*[@id=\"input-password\"]"));
    WebElement SubscribeButton = driver.findElement(By.xpath("//*[@id=\"input-newsletter\"]"));
    WebElement privacyPolicyButton = driver.findElement(By.xpath("//*[@id=\"form-register\"]/div/div/input"));
    WebElement SignupButtonBar = driver.findElement(By.xpath("//*[@id=\"account-login\"]/ul/li[3]/a"));
    WebElement ContinueButton = driver.findElement(By.xpath("//*[@id=\"form-register\"]/div/button"));

    //Method for login with user's data
    public void signup (){

    }
}

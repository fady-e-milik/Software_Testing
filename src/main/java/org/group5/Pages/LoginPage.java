package org.group5.Pages;

import org.group5.BaseClass;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class LoginPage extends BaseClass {
    public LoginPage (WebDriver driver) {
        super(driver);
    }

    //Declaring the main elements for the login page
    WebElement emailField = driver.findElement(By.xpath("//*[@id=\"input-email\"]"));
    WebElement passwdField = driver.findElement(By.xpath("//*[@id=\"input-password\"]"));
    WebElement loginBoxTitle = driver.findElement(By.xpath("//*[@id=\"form-login\"]/h2"));
    WebElement forgotPasswdButton = driver.findElement(By.xpath("//*[@id=\"form-login\"]/div[2]/a"));
    WebElement continueToSignupButton = driver.findElement(By.xpath("//*[@id=\"content\"]/div/div[1]/div/div/a"));
    WebElement LoginButtonBar = driver.findElement(By.xpath("//*[@id=\"account-login\"]/ul/li[3]/a"));
    WebElement LoginButton = driver.findElement(By.xpath("//*[@id=\"form-login\"]/div[3]/button"));

    //Method for login with user's data
    public void login (){

    }
}

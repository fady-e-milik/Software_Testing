package org.group5.Pages;

import org.group5.BaseClass;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class LoginPage extends BaseClass {

    private final WebDriver driver;

    // Locators
    private final By emailField = By.id("input-email");
    private final By passwdField = By.id("input-password");
    private final By forgotPasswdButton = By.xpath("//a[normalize-space()='Forgotten Password']");
    private final By continueToSignupButton = By.xpath("//a[normalize-space()='Continue']");
    private final By loginButton = By.xpath("//button[normalize-space()='Login']");
    private final By myAccountHeader = By.xpath("//h2[normalize-space()='My Account']");
    private final By loginErrorAlert = By.cssSelector(".alert.alert-danger");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    // --- Action Methods ---

    public void enterCredentials(String email, String password) {
        BaseClass.waitForElement(emailField).clear();
        BaseClass.waitForElement(emailField).sendKeys(email);
        BaseClass.waitForElement(passwdField).sendKeys(password);
    }

    public void clickLoginButton() {
        BaseClass.waitForClickable(loginButton).click();
    }

    public void clickForgotPasswdButton() {
        BaseClass.waitForClickable(forgotPasswdButton).click();
    }

    public void clickContinueToSignupButton() {
        BaseClass.waitForClickable(continueToSignupButton).click();
    }

    // --- Verification Methods ---

    public boolean isLoginPageDisplayed() {
        try {
            // Consider the login page displayed if the email and password inputs are present
            WebElement e1 = BaseClass.waitForElement(emailField, java.time.Duration.ofSeconds(5));
            WebElement e2 = BaseClass.waitForElement(passwdField, java.time.Duration.ofSeconds(5));
            return e1 != null && e2 != null;
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean isLoginSuccessful() {
        try {
            return BaseClass.waitForElement(myAccountHeader, java.time.Duration.ofSeconds(5)) != null;
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean isLoginErrorDisplayed() {
        try {
            return BaseClass.waitForElement(loginErrorAlert, java.time.Duration.ofSeconds(3)) != null;
        } catch (Exception ex) {
            return false;
        }
    }
}
package org.group5.Pages;

import org.group5.BaseClass;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import java.time.Duration;

public class LoginPage extends BaseClass {

    public LoginPage(WebDriver driver) {
        PageFactory.initElements(driver, this);
    }

    // --- Web Elements Declaration ---

    @FindBy(id = "input-email")
    private WebElement emailField;

    @FindBy(id = "input-password")
    private WebElement passwordField;

    @FindBy(xpath = "//a[contains(text(), 'Forgotten Password')]")
    private WebElement forgotPasswordButton;

    @FindBy(xpath = "//a[contains(text(), 'Continue')]")
    private WebElement continueToSignupButton;

    @FindBy(xpath = "//button[contains(text(), 'Login')]")
    private WebElement loginButton;

    @FindBy(xpath = "//h1[contains(text(), 'My Account')]")
    private WebElement myAccountHeader;

    @FindBy(xpath = "//h2[contains(text(), 'Returning Customer')]")
    private WebElement returningCustomerSection;

    @FindBy(css = ".alert.alert-danger")
    private WebElement loginErrorAlert;

    // --- Action Methods ---

    public void enterCredentials(String email, String password) {
        try {
            WebElement emailBox = BaseClass.waitForElement(emailField, Duration.ofSeconds(10));
            emailBox.clear();
            emailBox.sendKeys(email);
            
            WebElement passBox = BaseClass.waitForElement(passwordField, Duration.ofSeconds(10));
            passBox.sendKeys(password);
        } catch (Exception e) {
            throw new RuntimeException("Failed to enter credentials: " + e.getMessage(), e);
        }
    }

    public void clickLoginButton() {
        try {
            BaseClass.waitForClickable(loginButton, Duration.ofSeconds(10)).click();
        } catch (Exception e) {
            throw new RuntimeException("Failed to click login button: " + e.getMessage(), e);
        }
    }

    public void clickForgotPasswordButton() {
        try {
            BaseClass.waitForClickable(forgotPasswordButton, Duration.ofSeconds(10)).click();
        } catch (Exception e) {
            throw new RuntimeException("Failed to click forgot password button: " + e.getMessage(), e);
        }
    }

    public void clickContinueToSignup() {
        try {
            BaseClass.waitForClickable(continueToSignupButton, Duration.ofSeconds(10)).click();
        } catch (Exception e) {
            throw new RuntimeException("Failed to click continue to signup: " + e.getMessage(), e);
        }
    }

    // --- Verification Methods ---

    public boolean isLoginPageDisplayed() {
        try {
            return BaseClass.waitForElement(returningCustomerSection, Duration.ofSeconds(10)).isDisplayed();
        } catch (Exception e) {
            System.out.println("Login page not displayed: " + e.getMessage());
            return false;
        }
    }

    public boolean isLoginSuccessful() {
        try {
            return BaseClass.waitForElement(myAccountHeader, Duration.ofSeconds(10)).isDisplayed()
                    && myAccountHeader.getText().contains("My Account");
        } catch (Exception e) {
            System.out.println("Login success verification failed: " + e.getMessage());
            return false;
        }
    }

    public boolean isLoginErrorDisplayed() {
        try {
            return BaseClass.waitForElement(loginErrorAlert, Duration.ofSeconds(5)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getLoginErrorMessage() {
        try {
            return BaseClass.waitForElement(loginErrorAlert, Duration.ofSeconds(5)).getText();
        } catch (Exception e) {
            return "Error message not found";
        }
    }
}
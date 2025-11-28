package org.group5.Pages;

import org.group5.BaseClass;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.NoSuchElementException;

public class LoginPage extends BaseClass {

    public LoginPage (WebDriver driver) {
        PageFactory.initElements(driver, this);
    }

    // --- Web Elements Declaration using @FindBy ---

    @FindBy(id = "input-email")
    private WebElement emailField;

    @FindBy(id = "input-password")
    private WebElement passwdField;

    // Corrected locator for the login page title header for verification
    @FindBy(xpath = "//h1[normalize-space()='Login']")
    private WebElement pageTitleHeader;

    @FindBy(xpath = "//a[normalize-space()='Forgotten Password']")
    private WebElement forgotPasswdButton;

    // Locator for the "Continue" button under "New Customer" (to go to registration)
    @FindBy(xpath = "//a[normalize-space()='Continue']")
    private WebElement continueToSignupButton;

    @FindBy(xpath = "//button[normalize-space()='Login']")
    private WebElement loginButton;

    // Element to verify successful login (e.g., presence of "My Account" header)
    @FindBy(xpath = "//h2[normalize-space()='My Account']")
    private WebElement myAccountHeader;

    // Element for capturing login error message (e.g., invalid credentials)
    @FindBy(css = ".alert.alert-danger")
    private WebElement loginErrorAlert;


    // --- Action Methods ---

    public void enterCredentials(String email, String password) {
        emailField.sendKeys(email);
        passwdField.sendKeys(password);
    }

    public void clickLoginButton() {
        loginButton.click();
    }

    public void clickForgotPasswdButton() {
        forgotPasswdButton.click();
    }

    public void clickContinueToSignupButton() {
        continueToSignupButton.click();
    }

    // --- Verification Methods ---

    public boolean isLoginPageDisplayed() {
        try {
            return pageTitleHeader.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public boolean isLoginSuccessful() {
        try {
            return myAccountHeader.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public boolean isLoginErrorDisplayed() {
        try {
            return loginErrorAlert.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }
}
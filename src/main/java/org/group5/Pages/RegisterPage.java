package org.group5.Pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.group5.BaseClass;

public class RegisterPage {
    // No per-page WebDriver field required — we use BaseClass helper methods

    // Locators
    private final By firstNameField = By.id("input-firstname");
    private final By lastNameField = By.id("input-lastname");
    private final By emailField = By.id("input-email");
    private final By passwordField = By.id("input-password");
    private final By privacyPolicyCheckbox = By.name("agree");
    private final By telephoneField = By.id("input-telephone");
    private final By confirmPasswordField = By.id("input-confirm");
    private final By continueButton = By.cssSelector("input[value='Continue']");
    private final By successHeader = By.xpath("//*[@id=\"content\"]/h1"); // H1 on the success page

    private final org.openqa.selenium.WebDriver driver;

    public RegisterPage(org.openqa.selenium.WebDriver driver) {
        // Store the driver for potential per-page direct interactions
        this.driver = driver;
    }

    public void enterFirstName(String firstName) {
        BaseClass.waitForElement(firstNameField).sendKeys(firstName);
    }

    public void enterLastName(String lastName) {
        BaseClass.waitForElement(lastNameField).sendKeys(lastName);
    }

    public void enterEmail(String email) {
        BaseClass.waitForElement(emailField).sendKeys(email);
    }

    public void enterPassword(String password) {
        BaseClass.waitForElement(passwordField).sendKeys(password);
    }

    public void enterTelephone(String telephone) {
        BaseClass.waitForElement(telephoneField).sendKeys(telephone);
    }

    public void enterConfirmPassword(String confirmPassword) {
        BaseClass.waitForElement(confirmPasswordField).sendKeys(confirmPassword);
    }

    public void checkPrivacyPolicy() {
        // Use WebDriverWait to ensure the element is clickable before clicking
        BaseClass.waitForClickable(privacyPolicyCheckbox).click();
    }

    public void clickContinue() {
        BaseClass.waitForClickable(continueButton).click();
    }

    public String verifySuccessMessage() {
        WebElement successElement = BaseClass.waitForElement(successHeader);
        return successElement.getText();
    }
}
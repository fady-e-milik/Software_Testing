package org.group5.Pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class RegisterPage {
    // Instance variables to hold the browser reference and the explicit wait utility.
    private final WebDriver driver; // Made final as best practice, the reference should not change.
    private final WebDriverWait wait; // Made final.

    // Locators
    private final By firstNameField = By.id("input-firstname");
    private final By lastNameField = By.id("input-lastname");
    private final By emailField = By.id("input-email");
    private final By passwordField = By.id("input-password");
    private final By privacyPolicyCheckbox = By.name("agree");
    private final By continueButton = By.cssSelector("input[value='Continue']");
    private final By successHeader = By.xpath("//div[@id='content']/h1"); // H1 on the success page

    public RegisterPage(WebDriver driver) {
        this.driver = driver;
        // Initialize explicit wait for 10 seconds
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void enterFirstName(String firstName) {
        driver.findElement(firstNameField).sendKeys(firstName);
    }

    public void enterLastName(String lastName) {
        driver.findElement(lastNameField).sendKeys(lastName);
    }

    public void enterEmail(String email) {
        driver.findElement(emailField).sendKeys(email);
    }

    public void enterPassword(String password) {
        driver.findElement(passwordField).sendKeys(password);
    }

    public void checkPrivacyPolicy() {
        // Use WebDriverWait to ensure the element is clickable before clicking
        wait.until(ExpectedConditions.elementToBeClickable(privacyPolicyCheckbox)).click();
    }

    public void clickContinue() {
        wait.until(ExpectedConditions.elementToBeClickable(continueButton)).click();
    }

    public String verifySuccessMessage() {
        WebElement successElement = wait.until(ExpectedConditions.visibilityOfElementLocated(successHeader));
        return successElement.getText();
    }
}
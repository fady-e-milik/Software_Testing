package org.group5.Pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.group5.BaseClass;
import java.time.Duration;

/**
 * Page Object Model for My Account page
 */
public class MyAccountPage extends BaseClass {

    private final WebDriver driver;

    public MyAccountPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    // --- Web Elements Declaration ---

    @FindBy(xpath = "//h1[contains(text(), 'My Account')]")
    private WebElement myAccountHeader;

    @FindBy(linkText = "Edit Account")
    private WebElement editAccountLink;

    @FindBy(linkText = "Password")
    private WebElement passwordLink;

    @FindBy(linkText = "Address Book")
    private WebElement addressBookLink;

    @FindBy(linkText = "Payment Methods")
    private WebElement paymentMethodsLink;

    @FindBy(linkText = "Downloads")
    private WebElement downloadsLink;

    @FindBy(linkText = "Wish List")
    private WebElement wishListLink;

    @FindBy(linkText = "Order History")
    private WebElement orderHistoryLink;

    @FindBy(linkText = "Subscriptions")
    private WebElement subscriptionsLink;

    @FindBy(linkText = "Reward Points")
    private WebElement rewardPointsLink;

    @FindBy(linkText = "Returns")
    private WebElement returnsLink;

    @FindBy(linkText = "Transactions")
    private WebElement transactionsLink;

    @FindBy(xpath = "//input[@id='input-firstname']")
    private WebElement firstNameField;

    @FindBy(xpath = "//input[@id='input-lastname']")
    private WebElement lastNameField;

    @FindBy(xpath = "//input[@id='input-email']")
    private WebElement emailField;

    @FindBy(xpath = "//textarea[@id='input-newsletter']")
    private WebElement newsletterCheckbox;

    @FindBy(xpath = "//button[contains(text(), 'Continue')]")
    private WebElement continueButton;

    @FindBy(css = "div.alert-success")
    private WebElement successMessage;

    // --- Action Methods ---

    /**
     * Navigate to My Account page
     */
    public void navigateToMyAccount() {
        driver.get(BaseClass.getBaseUrl() + "index.php?route=account/account");
        BaseClass.waitForSiteToLoad(driver, Duration.ofSeconds(15));
    }

    /**
     * Click Edit Account link
     */
    public void clickEditAccount() {
        try {
            BaseClass.waitForClickable(editAccountLink, Duration.ofSeconds(10)).click();
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Click Address Book link
     */
    public void clickAddressBook() {
        try {
            BaseClass.waitForClickable(addressBookLink, Duration.ofSeconds(10)).click();
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Click Order History link
     */
    public void clickOrderHistory() {
        try {
            BaseClass.waitForClickable(orderHistoryLink, Duration.ofSeconds(10)).click();
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Click Wish List link
     */
    public void clickWishList() {
        try {
            BaseClass.waitForClickable(wishListLink, Duration.ofSeconds(10)).click();
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Click Password link
     */
    public void clickPassword() {
        try {
            BaseClass.waitForClickable(passwordLink, Duration.ofSeconds(10)).click();
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Edit account information
     */
    public void editAccountInfo(String firstName, String lastName, String email) {
        try {
            WebElement fnField = BaseClass.waitForElement(firstNameField, Duration.ofSeconds(10));
            fnField.clear();
            fnField.sendKeys(firstName);

            WebElement lnField = BaseClass.waitForElement(lastNameField, Duration.ofSeconds(10));
            lnField.clear();
            lnField.sendKeys(lastName);

            WebElement emailBox = BaseClass.waitForElement(emailField, Duration.ofSeconds(10));
            emailBox.clear();
            emailBox.sendKeys(email);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Save account changes
     */
    public void saveChanges() {
        try {
            BaseClass.waitForClickable(continueButton, Duration.ofSeconds(10)).click();
        } catch (Exception e) {
            throw e;
        }
    }

    // --- Verification Methods ---

    /**
     * Verify My Account page is displayed
     */
    public boolean isMyAccountPageDisplayed() {
        try {
            return BaseClass.waitForElement(myAccountHeader, Duration.ofSeconds(10)).isDisplayed();
        } catch (Exception e) {
            System.out.println("My Account page not displayed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Verify account was updated successfully
     */
    public boolean isAccountUpdatedSuccessfully() {
        try {
            return BaseClass.waitForElement(successMessage, Duration.ofSeconds(5)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get success message
     */
    public String getSuccessMessage() {
        try {
            return BaseClass.waitForElement(successMessage, Duration.ofSeconds(5)).getText();
        } catch (Exception e) {
            return "Message not found";
        }
    }

    /**
     * Verify Edit Account link is visible
     */
    public boolean isEditAccountLinkVisible() {
        try {
            return BaseClass.waitForElement(editAccountLink, Duration.ofSeconds(5)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Verify Order History link is visible
     */
    public boolean isOrderHistoryLinkVisible() {
        try {
            return BaseClass.waitForElement(orderHistoryLink, Duration.ofSeconds(5)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Verify Address Book link is visible
     */
    public boolean isAddressBookLinkVisible() {
        try {
            return BaseClass.waitForElement(addressBookLink, Duration.ofSeconds(5)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}

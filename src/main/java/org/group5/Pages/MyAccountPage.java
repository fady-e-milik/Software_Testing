package org.group5.Pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
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

    @FindBy(css = "#content > h1")
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
        BaseClass.waitForSiteToLoad(BaseClass.getDriver(), Duration.ofSeconds(15));
    }

    /**
     * Click Edit Account link
     */
    public void clickEditAccount() {
        BaseClass.waitForClickable(By.linkText("Edit Account")).click();
    }

    /**
     * Click Address Book link
     */
    public void clickAddressBook() {
        BaseClass.waitForClickable(By.linkText("Address Book")).click();
    }

    /**
     * Click Order History link
     */
    public void clickOrderHistory() {
        BaseClass.waitForClickable(By.linkText("Order History")).click();
    }

    /**
     * Click Wish List link
     */
    public void clickWishList() {
        BaseClass.waitForClickable(By.linkText("Wish List")).click();
    }

    /**
     * Click Password link
     */
    public void clickPassword() {
        BaseClass.waitForClickable(By.linkText("Password")).click();
    }

    /**
     * Edit account information
     */
    public void editAccountInfo(String firstName, String lastName, String email) {
        WebElement fnField = BaseClass.waitForElement(By.xpath("//input[@id='input-firstname']"), Duration.ofSeconds(10));
        fnField.clear();
        fnField.sendKeys(firstName);

        WebElement lnField = BaseClass.waitForElement(By.xpath("//input[@id='input-lastname']"), Duration.ofSeconds(10));
        lnField.clear();
        lnField.sendKeys(lastName);

        WebElement emailBox = BaseClass.waitForElement(By.xpath("//input[@id='input-email']"), Duration.ofSeconds(10));
        emailBox.clear();
        emailBox.sendKeys(email);
    }

    /**
     * Save account changes
     */
    public void saveChanges() {
        BaseClass.waitForClickable(By.xpath("//button[contains(text(), 'Continue')]")).click();
    }

    // --- Verification Methods ---

    /**
     * Verify My Account page is displayed
     */
    public boolean isMyAccountPageDisplayed() {
        try {
            return BaseClass.waitForElement(By.cssSelector("#content > h1")).isDisplayed();
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
            return BaseClass.waitForElement(By.cssSelector("div.alert-success")).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get success message
     */
    public String getSuccessMessage() {
        try {
            return BaseClass.waitForElement(By.cssSelector("div.alert-success")).getText();
        } catch (Exception e) {
            return "Message not found";
        }
    }

    /**
     * Verify Edit Account link is visible
     */
    public boolean isEditAccountLinkVisible() {
        try {
            return BaseClass.waitForElement(By.linkText("Edit Account")).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Verify Order History link is visible
     */
    public boolean isOrderHistoryLinkVisible() {
        try {
            return BaseClass.waitForElement(By.linkText("Order History")).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Verify Address Book link is visible
     */
    public boolean isAddressBookLinkVisible() {
        try {
            return BaseClass.waitForElement(By.linkText("Address Book")).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}

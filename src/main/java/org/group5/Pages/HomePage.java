package org.group5.Pages;

import org.group5.BaseClass;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.NoSuchElementException;

public class HomePage extends BaseClass {

    public HomePage (WebDriver driver) {
        PageFactory.initElements(driver, this);
    }

    // --- Web Elements Declaration ---

    // Top Navigation Links (Links usually found in the header)
    @FindBy(xpath = "//span[normalize-space()='My Account']")
    private WebElement myAccountMenu;

    @FindBy(linkText = "Register")
    private WebElement registerLink;

    @FindBy(linkText = "Login")
    private WebElement loginLink;

    // Search Bar
    @FindBy(name = "search")
    private WebElement searchInput;

    @FindBy(xpath = "//button[@class='btn btn-default btn-lg']")
    private WebElement searchButton;

    // Unique element to confirm page presence (e.g., the main banner/slider)
    @FindBy(id = "slideshow0")
    private WebElement mainSliderBanner;

    // Example Featured Product Element (first product link)
    @FindBy(xpath = "//div[@class='product-layout']//h4/a")
    private WebElement firstFeaturedProductLink;


    // --- Action Methods ---

    public void navigateToRegistrationPage() {
        myAccountMenu.click();
        registerLink.click();
    }

    public void navigateToLoginPage() {
        myAccountMenu.click();
        loginLink.click();
    }

    public void searchForProduct(String productName) {
        searchInput.sendKeys(productName);
        searchButton.click();
    }

    public void clickFirstFeaturedProduct() {
        firstFeaturedProductLink.click();
    }

    // --- Verification Methods ---

    public boolean isHomePageDisplayed() {
        try {
            return mainSliderBanner.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public boolean isMyAccountMenuPresent() {
        try {
            return myAccountMenu.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }
}
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
    @FindBy(xpath = "//*[@id=\"top\"]/div/div/div[2]/ul/li[2]/div/a")
    private WebElement myAccountMenu;

    @FindBy(xpath = "//*[@id=\"top\"]/div/div/div[2]/ul/li[2]/div/ul/li[1]/a")
    private WebElement registerLink;

    @FindBy(xpath = "//*[@id=\"top\"]/div/div/div[2]/ul/li[2]/div/ul/li[2]/a")
    private WebElement loginLink;

    // Search Bar
    @FindBy(name = "search")
    private WebElement searchInput;

    @FindBy(xpath = "//*[@id=\"container\"]/header/div/div/div[2]/form/button")
    private WebElement searchButton;

    // Unique element to confirm page presence (e.g., the main banner/slider)
    @FindBy(xpath = "//*[@id=\"carousel-banner-0\"]/div[2]/div[1]")
    private WebElement mainSliderBanner;

    // Example Featured Product Element (first product link)
    @FindBy(xpath = "//*[@id=\"content\"]/div[2]/div[1]/div/div[2]/div/h4/a")
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
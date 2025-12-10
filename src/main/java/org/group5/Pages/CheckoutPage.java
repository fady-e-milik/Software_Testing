package org.group5.Pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.group5.BaseClass;
import java.time.Duration;

/**
 * Page Object Model for Checkout page
 */
public class CheckoutPage extends BaseClass {

    private final WebDriver driver;

    public CheckoutPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    // --- Web Elements Declaration ---

    @FindBy(xpath = "//h1[contains(text(), 'Checkout')]")
    private WebElement checkoutHeader;

    // Billing Address Section
    @FindBy(xpath = "//input[@id='input-billing-firstname']")
    private WebElement billingFirstName;

    @FindBy(xpath = "//input[@id='input-billing-lastname']")
    private WebElement billingLastName;

    @FindBy(xpath = "//input[@id='input-billing-email']")
    private WebElement billingEmail;

    @FindBy(xpath = "//input[@id='input-billing-address-1']")
    private WebElement billingAddress;

    @FindBy(xpath = "//input[@id='input-billing-city']")
    private WebElement billingCity;

    @FindBy(xpath = "//input[@id='input-billing-postcode']")
    private WebElement billingPostcode;

    // Shipping Information
    @FindBy(xpath = "//input[@name='shipping_method'][1]")
    private WebElement shippingMethodRadio;

    @FindBy(xpath = "//button[contains(text(), 'Continue')]")
    private WebElement continueButton;

    // Order Confirmation
    @FindBy(xpath = "//h1[contains(text(), 'Your order has been placed')]")
    private WebElement orderConfirmationHeader;

    @FindBy(xpath = "//div[@class='alert alert-success']")
    private WebElement successAlert;

    @FindBy(xpath = "//a[contains(text(), 'Continue')]")
    private WebElement continueAfterOrderButton;

    // Payment Method Section
    @FindBy(xpath = "//input[@name='payment_method']")
    private WebElement paymentMethodRadio;

    @FindBy(xpath = "//textarea[@id='input-comment']")
    private WebElement orderComments;

    // Order Summary
    @FindBy(xpath = "//strong[contains(text(), 'Sub-Total:')]/following::td")
    private WebElement orderSubTotal;

    @FindBy(xpath = "//strong[contains(text(), 'Total:')]/following::td")
    private WebElement orderTotal;

    // --- Action Methods ---

    /**
     * Navigate to Checkout page
     */
    public void navigateToCheckout() {
        driver.get(BaseClass.getBaseUrl() + "index.php?route=checkout/checkout");
        BaseClass.waitForSiteToLoad(driver, Duration.ofSeconds(15));
    }

    /**
     * Fill billing address information
     */
    public void fillBillingAddress(String firstName, String lastName, String email, String address, String city, String postcode) {
        try {
            // Try to get fresh elements by locator (handles cases where PageFactory fields may be stale or not visible yet)
            By fnLocator = By.id("input-billing-firstname");
            By lnLocator = By.id("input-billing-lastname");
            By emailLocator = By.id("input-billing-email");
            By addressLocator = By.id("input-billing-address-1");
            By cityLocator = By.id("input-billing-city");
            By postcodeLocator = By.id("input-billing-postcode");

            // Fast attempt: wait briefly for the billing firstname to be visible
            try {
                WebElement fnField = BaseClass.waitForElement(fnLocator, Duration.ofSeconds(10));
                fnField.clear();
                fnField.sendKeys(firstName);

                WebElement lnField = BaseClass.waitForElement(lnLocator, Duration.ofSeconds(10));
                lnField.clear();
                lnField.sendKeys(lastName);

                WebElement emailField = BaseClass.waitForElement(emailLocator, Duration.ofSeconds(10));
                emailField.clear();
                emailField.sendKeys(email);

                WebElement addressField = BaseClass.waitForElement(addressLocator, Duration.ofSeconds(10));
                addressField.clear();
                addressField.sendKeys(address);

                WebElement cityField = BaseClass.waitForElement(cityLocator, Duration.ofSeconds(10));
                cityField.clear();
                cityField.sendKeys(city);

                WebElement postcodeField = BaseClass.waitForElement(postcodeLocator, Duration.ofSeconds(10));
                postcodeField.clear();
                postcodeField.sendKeys(postcode);

                return; // done
            } catch (RuntimeException re) {
                // The billing fields didn't become visible within the short wait. We'll try to reveal the billing form.
            }

            // Attempt to reveal billing form for logged-out/guest checkout flows
            try {
                // If a 'guest' checkout radio is present, select it
                By guestRadio = By.xpath("//input[@name='account' and (@value='guest' or contains(@value,'guest'))]");
                try {
                    WebElement guest = BaseClass.waitForClickable(guestRadio);
                    guest.click();
                } catch (Exception ignored) {
                    // Not present - continue with other strategies
                }

                // For logged-in users OpenCart sometimes shows an address selector instead of the form.
                // Try to choose the 'New Address' option via radio or select element.
                try {
                    By newAddrRadio = By.xpath("//input[@name='payment_address' and (contains(@value,'new') or @value='new')]");
                    WebElement newRadio = BaseClass.waitForClickable(newAddrRadio);
                    newRadio.click();
                } catch (Exception ignored) {
                    // try select fallback
                    try {
                        By addressSelect = By.xpath("//select[contains(@id,'address') or contains(@name,'address')]");
                        WebElement sel = BaseClass.waitForElement(addressSelect, Duration.ofSeconds(3));
                        // choose option with text containing 'new' (case-insensitive) using JS
                        try {
                            String script = "var s = arguments[0]; for(var i=0;i<s.options.length;i++){ if(s.options[i].text.toLowerCase().indexOf('new')!==-1){ s.selectedIndex = i; s.dispatchEvent(new Event('change')); break; } }";
                            BaseClass.executeScript(script, sel);
                        } catch (Exception jsEx) {
                            // ignore JS fallback failures
                        }
                    } catch (Exception ignored2) {
                        // not present - continue
                    }
                }

                // Several checkout pages use a "Continue" button to move between panels; try to click visible continue buttons
                By continueBtnXpath = By.xpath("//button[contains(normalize-space(.),'Continue')]");
                try {
                    WebElement cont = BaseClass.waitForClickable(continueBtnXpath);
                    cont.click();
                } catch (Exception ignored) {
                    // If that fails, try an input[type=button] with value Continue
                    try {
                        By contInput = By.xpath("//input[@type='button' and (translate(@value,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')='continue' or @value='Continue')]");
                        WebElement cont2 = BaseClass.waitForClickable(contInput);
                        cont2.click();
                    } catch (Exception ignored2) {
                        // give up on clicking continue - next we'll wait longer for the billing fields
                    }
                }
            } catch (Exception e) {
                // ignore and proceed to try waiting longer below
            }

            // After attempting to reveal, wait longer for the billing fields and set them
            WebElement fnField = BaseClass.waitForElement(fnLocator, Duration.ofSeconds(15));
            fnField.clear();
            fnField.sendKeys(firstName);

            WebElement lnField = BaseClass.waitForElement(lnLocator, Duration.ofSeconds(10));
            lnField.clear();
            lnField.sendKeys(lastName);

            // email field may not be present for logged-in users; guard it
            try {
                WebElement emailField = BaseClass.waitForElement(emailLocator, Duration.ofSeconds(5));
                emailField.clear();
                emailField.sendKeys(email);
            } catch (Exception ignored) {
                // logged-in flows often do not require email in the billing panel
            }

            WebElement addressField = BaseClass.waitForElement(addressLocator, Duration.ofSeconds(10));
            addressField.clear();
            addressField.sendKeys(address);

            WebElement cityField = BaseClass.waitForElement(cityLocator, Duration.ofSeconds(10));
            cityField.clear();
            cityField.sendKeys(city);

            WebElement postcodeField = BaseClass.waitForElement(postcodeLocator, Duration.ofSeconds(10));
            postcodeField.clear();
            postcodeField.sendKeys(postcode);

        } catch (Exception e) {
            // Save debug artifacts to help diagnose hidden/conditional checkout flows
            try {
                BaseClass.saveDebugArtifacts(driver, "fillBillingAddressFailure");
            } catch (Exception ex) {
                System.err.println("Failed to save debug artifacts: " + ex.getMessage());
            }
            throw new RuntimeException("Failed to fill billing address: " + e.getMessage(), e);
        }
    }

    /**
     * Select shipping method
     */
    public void selectShippingMethod() {
        BaseClass.waitForClickable(shippingMethodRadio, Duration.ofSeconds(10)).click();
    }

    /**
     * Select payment method
     */
    public void selectPaymentMethod() {
        BaseClass.waitForClickable(paymentMethodRadio, Duration.ofSeconds(10)).click();
    }

    /**
     * Add order comments
     */
    public void addOrderComments(String comments) {
        WebElement commentsField = BaseClass.waitForElement(orderComments, Duration.ofSeconds(10));
        commentsField.sendKeys(comments);
    }

    /**
     * Click Continue button to proceed checkout
     */
    public void clickContinue() {
        try {
            BaseClass.waitForClickable(continueButton, Duration.ofSeconds(10)).click();
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Place order - final step
     */
    public void placeOrder() {
        try {
            BaseClass.waitForClickable(continueButton, Duration.ofSeconds(10)).click();
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Get order total
     */
    public String getOrderTotal() {
        try {
            String total = BaseClass.waitForElement(orderTotal, Duration.ofSeconds(10)).getText();
            return total;
        } catch (Exception e) {
            return "0.00";
        }
    }

    /**
     * Get order subtotal
     */
    public String getOrderSubTotal() {
        try {
            String subTotal = BaseClass.waitForElement(orderSubTotal, Duration.ofSeconds(10)).getText();
            return subTotal;
        } catch (Exception e) {
            return "0.00";
        }
    }

    // --- Verification Methods ---

    /**
     * Verify Checkout page is displayed
     */
    public boolean isCheckoutPageDisplayed() {
        try {
            return BaseClass.waitForElement(checkoutHeader, Duration.ofSeconds(10)).isDisplayed();
        } catch (Exception e) {
            System.out.println("Checkout page not displayed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Verify order has been placed successfully
     */
    public boolean isOrderPlacedSuccessfully() {
        try {
            return BaseClass.waitForElement(orderConfirmationHeader, Duration.ofSeconds(15)).isDisplayed();
        } catch (Exception e) {
            System.out.println("Order confirmation not found: " + e.getMessage());
            return false;
        }
    }

    /**
     * Verify success alert is displayed
     */
    public boolean isSuccessAlertDisplayed() {
        try {
            return BaseClass.waitForElement(successAlert, Duration.ofSeconds(10)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get success message
     */
    public String getSuccessMessage() {
        try {
            return BaseClass.waitForElement(successAlert, Duration.ofSeconds(10)).getText();
        } catch (Exception e) {
            return "Message not found";
        }
    }

    /**
     * Verify billing address fields are visible
     */
    public boolean isBillingAddressFieldsVisible() {
        try {
            return BaseClass.waitForElement(billingFirstName, Duration.ofSeconds(5)).isDisplayed()
                    && BaseClass.waitForElement(billingAddress, Duration.ofSeconds(5)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Click Continue after successful order
     */
    public void clickContinueAfterOrder() {
        try {
            BaseClass.waitForClickable(continueAfterOrderButton, Duration.ofSeconds(10)).click();
        } catch (Exception e) {
            throw e;
        }
    }
}

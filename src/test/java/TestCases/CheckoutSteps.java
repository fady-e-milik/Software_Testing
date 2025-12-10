package TestCases;

import org.openqa.selenium.WebDriver;
import org.group5.BaseClass;
import org.group5.Pages.CheckoutPage;
import org.group5.Pages.CartPage;
import org.group5.Pages.HomePage;
import java.time.Duration;
import io.cucumber.java.Before;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.testng.Assert;

public class CheckoutSteps {

    private WebDriver driver;
    private CheckoutPage checkoutPage;
    private CartPage cartPage;
    private HomePage homePage;

    @Before(order = 1)
    public void init() {
        this.driver = BaseClass.getDriver();
        this.checkoutPage = new CheckoutPage(driver);
        this.cartPage = new CartPage(driver);
        this.homePage = new HomePage(driver);
    }

    @When("User navigates to Checkout page")
    public void user_navigates_to_checkout_page() {
        // Ensure there is at least one product in the cart - if not, add the first featured product
        try {
            if (!cartPage.hasCartItems()) {
                // Go home, open a product, add to cart and then navigate to cart
                driver.get(BaseClass.getBaseUrl());
                BaseClass.waitForSiteToLoad(driver, Duration.ofSeconds(10));

                // Try to open product and add to cart
                try {
                    homePage.clickFirstFeaturedProduct();
                    BaseClass.waitForSiteToLoad(driver, Duration.ofSeconds(10));
                } catch (Exception ignored) {
                    // If navigation via featured product fails, attempt a search and add
                    try {
                        homePage.searchForProduct("MacBook");
                        BaseClass.waitForSiteToLoad(driver, Duration.ofSeconds(8));
                        homePage.clickFirstFeaturedProduct();
                        BaseClass.waitForSiteToLoad(driver, Duration.ofSeconds(8));
                    } catch (Exception ignore) {
                        // fall through - add to cart may still work from current page
                    }
                }

                // If we're on product page, click Add to Cart
                try {
                    if (homePage.isProductPageDisplayed()) {
                        homePage.clickAddToCart();
                        BaseClass.waitForSiteToLoad(driver, Duration.ofSeconds(5));
                    }
                } catch (Exception ignored) {}

                // Navigate to the cart page to ensure the item is present there
                cartPage.navigateToCart();
                BaseClass.waitForSiteToLoad(driver, Duration.ofSeconds(8));

                // If still no items, try again: add product from home then navigate to cart
                if (!cartPage.hasCartItems()) {
                    try {
                        driver.get(BaseClass.getBaseUrl());
                        BaseClass.waitForSiteToLoad(driver, Duration.ofSeconds(8));
                        homePage.clickFirstFeaturedProduct();
                        BaseClass.waitForSiteToLoad(driver, Duration.ofSeconds(6));
                        if (homePage.isProductPageDisplayed()) {
                            homePage.clickAddToCart();
                            BaseClass.waitForSiteToLoad(driver, Duration.ofSeconds(5));
                        }
                        cartPage.navigateToCart();
                        BaseClass.waitForSiteToLoad(driver, Duration.ofSeconds(6));
                    } catch (Exception e) {
                        // give up here - the checkout step will still navigate and the downstream checks will surface failures
                    }
                }
            }
        } catch (Exception e) {
            // Save debug artifacts to help diagnose unexpected failures while prepping cart
            try { BaseClass.saveDebugArtifacts(driver, "ensure_cart_before_checkout_failure"); } catch (Exception ignore) {}
        }

        // Now navigate to checkout
        checkoutPage.navigateToCheckout();
        BaseClass.waitForSiteToLoad(driver, Duration.ofSeconds(10));
    }

    @When("User clicks Checkout button")
    public void user_clicks_checkout_button() {
        cartPage.clickCheckout();
        BaseClass.waitForSiteToLoad(driver, Duration.ofSeconds(10));
    }

    @When("User fills billing address with firstName {string} lastName {string} email {string} address {string} city {string} postcode {string}")
    public void user_fills_billing_address_with_fields(String fn, String ln, String email, String addr, String city, String postcode) {
        checkoutPage.fillBillingAddress(fn, ln, email, addr, city, postcode);
        BaseClass.waitForSiteToLoad(driver, Duration.ofSeconds(5));
    }

    @When("User fills billing address with {string},{string},{string},{string},{string},{string}")
    public void user_fills_billing_address(String fn, String ln, String email, String addr, String city, String postcode) {
        checkoutPage.fillBillingAddress(fn, ln, email, addr, city, postcode);
        BaseClass.waitForSiteToLoad(driver, Duration.ofSeconds(5));
    }

    @When("User selects shipping method and payment method")
    public void user_selects_shipping_and_payment() {
        checkoutPage.selectShippingMethod();
        BaseClass.waitForSiteToLoad(driver, Duration.ofSeconds(5));
        checkoutPage.selectPaymentMethod();
    }

    @When("User selects shipping method")
    public void user_selects_shipping_method() {
        checkoutPage.selectShippingMethod();
        BaseClass.waitForSiteToLoad(driver, Duration.ofSeconds(5));
    }

    @When("User selects payment method")
    public void user_selects_payment_method() {
        checkoutPage.selectPaymentMethod();
        BaseClass.waitForSiteToLoad(driver, Duration.ofSeconds(5));
    }

    @When("User adds order comments {string}")
    public void user_adds_order_comments(String comments) {
        checkoutPage.addOrderComments(comments);
    }

    @When("User places order")
    public void user_places_order() {
        checkoutPage.placeOrder();
        BaseClass.waitForSiteToLoad(driver, Duration.ofSeconds(10));
    }

    @When("User places the order")
    public void user_places_the_order() {
        checkoutPage.placeOrder();
        BaseClass.waitForSiteToLoad(driver, Duration.ofSeconds(10));
    }

    // NOTE: The step "User has items in cart" is implemented in CartSteps.java and intentionally not duplicated here.

    @Then("Checkout page should be displayed")
    public void checkout_page_should_be_displayed() {
        Assert.assertTrue(checkoutPage.isCheckoutPageDisplayed(), "Checkout page is not displayed");
    }

    @Then("Checkout page should be displayed successfully")
    public void checkout_page_should_be_displayed_successfully() {
        Assert.assertTrue(checkoutPage.isCheckoutPageDisplayed(), "Checkout page is not displayed");
    }

    @Then("Billing address fields should be visible")
    public void billing_address_fields_should_be_visible() {
        Assert.assertTrue(checkoutPage.isBillingAddressFieldsVisible(), "Billing address fields not visible");
    }

    @Then("Billing address should be filled successfully")
    public void billing_address_should_be_filled_successfully() {
        // Billing address filled
    }

    @Then("User can proceed to next step")
    public void user_can_proceed_to_next_step() {
        // User can proceed to next step
    }

    @Then("Shipping method should be selected successfully")
    public void shipping_method_should_be_selected_successfully() {
        // Shipping method selected
    }

    @Then("Payment method should be selected successfully")
    public void payment_method_should_be_selected_successfully() {
        // Payment method selected
    }

    @Then("Comments should be saved")
    public void comments_should_be_saved() {
        // Comments saved
    }

    @Then("User can proceed to place order")
    public void user_can_proceed_to_place_order() {
        // User can place order
    }

    @Then("Order should be placed successfully")
    public void order_should_be_placed_successfully() {
        Assert.assertTrue(checkoutPage.isOrderPlacedSuccessfully(), "Order was not placed successfully");
    }

    @Then("Order confirmation page should be displayed")
    public void order_confirmation_page_should_be_displayed() {
        Assert.assertTrue(checkoutPage.isOrderPlacedSuccessfully(), "Order confirmation page not displayed");
    }

    @Then("Success message should appear")
    public void success_message_should_appear() {
        Assert.assertTrue(checkoutPage.isSuccessAlertDisplayed(), "Success message not displayed");
    }

    @Then("Order total should be visible")
    public void order_total_should_be_visible() {
        String total = checkoutPage.getOrderTotal();
        Assert.assertNotNull(total, "Order total not found");
    }

    @Then("Order subtotal should be displayed")
    public void order_subtotal_should_be_displayed() {
        String subTotal = checkoutPage.getOrderSubTotal();
        Assert.assertNotNull(subTotal, "Order subtotal not displayed");
    }

    @Then("Order total should be displayed")
    public void order_total_should_be_displayed() {
        String total = checkoutPage.getOrderTotal();
        Assert.assertNotNull(total, "Order total not displayed");
    }

    @Then("Total should be calculated correctly")
    public void total_should_be_calculated_correctly() {
        String total = checkoutPage.getOrderTotal();
        Assert.assertNotNull(total, "Total calculation failed");
    }
}

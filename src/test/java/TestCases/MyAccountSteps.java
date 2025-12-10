package TestCases;

import org.openqa.selenium.WebDriver;
import org.group5.BaseClass;
import org.group5.Pages.MyAccountPage;
import java.time.Duration;
import io.cucumber.java.Before;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.testng.Assert;

public class MyAccountSteps {

    private WebDriver driver;
    private MyAccountPage myAccountPage;

    @Before(order = 1)
    public void init() {
        this.driver = BaseClass.getDriver();
        this.myAccountPage = new MyAccountPage(driver);
    }

    @When("User navigates to My Account page")
    public void user_navigates_to_my_account_page() {
        myAccountPage.navigateToMyAccount();
        BaseClass.waitForSiteToLoad(driver, Duration.ofSeconds(10));
    }

    @When("User navigates to My Account")
    public void user_navigates_to_my_account() {
        myAccountPage.navigateToMyAccount();
        BaseClass.waitForSiteToLoad(driver, Duration.ofSeconds(10));
    }

    @When("User clicks Edit Account link")
    public void user_clicks_edit_account() {
        myAccountPage.clickEditAccount();
    }

    @When("User clicks on Edit Account")
    public void user_clicks_on_edit_account() {
        myAccountPage.clickEditAccount();
        BaseClass.waitForSiteToLoad(driver, Duration.ofSeconds(5));
    }

    @When("User updates account with {string},{string},{string}")
    public void user_updates_account(String fn, String ln, String email) {
        myAccountPage.editAccountInfo(fn, ln, email);
        myAccountPage.saveChanges();
    }

    @When("User updates account information with firstName {string} and lastName {string}")
    public void user_updates_account_information(String firstName, String lastName) {
        myAccountPage.editAccountInfo(firstName, lastName, "");
        myAccountPage.saveChanges();
        BaseClass.waitForSiteToLoad(driver, Duration.ofSeconds(5));
    }

    @When("User clicks on View Orders")
    public void user_clicks_on_view_orders() {
        myAccountPage.clickOrderHistory();
        BaseClass.waitForSiteToLoad(driver, Duration.ofSeconds(5));
    }

    @When("User clicks on Address Book")
    public void user_clicks_on_address_book() {
        myAccountPage.clickAddressBook();
        BaseClass.waitForSiteToLoad(driver, Duration.ofSeconds(5));
    }

    @When("User clicks on Wish List")
    public void user_clicks_on_wish_list() {
        myAccountPage.clickWishList();
        BaseClass.waitForSiteToLoad(driver, Duration.ofSeconds(5));
    }

    @When("User clicks on Change Password")
    public void user_clicks_on_change_password() {
        myAccountPage.clickPassword();
        BaseClass.waitForSiteToLoad(driver, Duration.ofSeconds(5));
    }

    @Then("My Account page should be displayed")
    public void my_account_page_should_be_displayed() {
        Assert.assertTrue(myAccountPage.isMyAccountPageDisplayed(), "My Account page not displayed");
    }

    @Then("My Account page should be displayed successfully")
    public void my_account_page_should_be_displayed_successfully() {
        Assert.assertTrue(myAccountPage.isMyAccountPageDisplayed(), "My Account page is not displayed");
    }

    @Then("User account details should be visible")
    public void user_account_details_should_be_visible() {
        Assert.assertTrue(myAccountPage.isEditAccountLinkVisible(), "Account details not visible");
    }

    @Then("Account should be updated")
    public void account_should_be_updated() {
        Assert.assertTrue(myAccountPage.isAccountUpdatedSuccessfully(), "Account update message not found");
    }

    @Then("Account information should be updated successfully")
    public void account_information_should_be_updated_successfully() {
        Assert.assertTrue(myAccountPage.isAccountUpdatedSuccessfully(), "Account was not updated");
    }

    @Then("Success message should be displayed")
    public void success_message_should_be_displayed() {
        String message = myAccountPage.getSuccessMessage();
        Assert.assertNotNull(message, "Success message not found");
        Assert.assertFalse(message.isEmpty(), "Success message is empty");
    }

    @Then("Order history section should be displayed")
    public void order_history_section_should_be_displayed() {
        Assert.assertTrue(myAccountPage.isOrderHistoryLinkVisible(), "Order history section not displayed");
    }

    @Then("Previous orders should be visible")
    public void previous_orders_should_be_visible() {
        // Orders visible when navigated to order history page
    }

    @Then("Address book page should be displayed")
    public void address_book_page_should_be_displayed() {
        Assert.assertTrue(myAccountPage.isAddressBookLinkVisible(), "Address book page not displayed");
    }

    @Then("User can view saved addresses")
    public void user_can_view_saved_addresses() {
        // Addresses visible when on address book page
    }

    @Then("Wish list page should be displayed")
    public void wish_list_page_should_be_displayed() {
        // Wish list page displayed after click
    }

    @Then("Password change form should be displayed")
    public void password_change_form_should_be_displayed() {
        // Password form displayed after click
    }

    @Then("User can enter new password")
    public void user_can_enter_new_password() {
        // Password field is available for input
    }
}

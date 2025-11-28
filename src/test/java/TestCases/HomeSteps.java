package TestCases;

import org.openqa.selenium.WebDriver;
import org.group5.BaseClass;
import java.time.Duration;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
import org.testng.Assert;

import org.group5.Pages.HomePage;

public class HomeSteps {

    private WebDriver driver;
    private HomePage homePage;

    public HomeSteps() {
        // Avoid initializing driver here as Hooks run later; initialize in @Before
    }

    @Before(order = 1)
    public void init() {
        this.driver = BaseClass.getDriver();
        this.homePage = new HomePage(this.driver);
    }

    // --- GIVEN Steps ---

    @Given("I am on the OpenCart home page")
    public void i_am_on_the_opencart_home_page() {
        // Navigates to the base URL of the demo site
        driver.get(BaseClass.getBaseUrl());
        BaseClass.waitForSiteToLoad(driver, Duration.ofSeconds(30));
        Assert.assertTrue(homePage.isHomePageDisplayed(),
                "Verification Failure: OpenCart Home page is not displayed.");
    }

    // --- WHEN Steps ---

    @When("I search for product {string}")
    public void i_search_for_product(String product) {
        homePage.searchForProduct(product);
    }

    @When("I click the Login link in the header")
    public void i_click_the_login_link_in_the_header() {
        homePage.navigateToLoginPage();
        BaseClass.waitForSiteToLoad(driver, Duration.ofSeconds(15));
    }

    @When("I click the Register link in the header")
    public void i_click_the_register_link_in_the_header() {
        homePage.navigateToRegistrationPage();
        BaseClass.waitForSiteToLoad(driver, Duration.ofSeconds(15));
    }

    @Then("I should be successfully navigated to the Login Page")
    public void i_should_be_successfully_navigated_to_the_login_page() {
        // Verify current URL contains the login route
        org.testng.Assert.assertTrue(driver.getCurrentUrl().contains("route=account/login"),
                "Failure: Expected to be on login page but URL was " + driver.getCurrentUrl());
    }

    @Then("I should be successfully navigated to the Registration Page")
    public void i_should_be_successfully_navigated_to_the_registration_page() {
        org.testng.Assert.assertTrue(driver.getCurrentUrl().contains("route=account/register"),
                "Failure: Expected to be on registration page but URL was " + driver.getCurrentUrl());
    }

    // --- THEN Steps ---

    @Then("I should be navigated to the search results page for {string}")
    public void i_should_be_navigated_to_the_search_results_page(String product) {
        // Verification: Check the URL or a specific search results element (assuming a SearchPage exists)
        Assert.assertTrue(driver.getCurrentUrl().contains("search=" + product),
                "Failure: Did not navigate to the search results page for: " + product);
        // Note: In a full framework, this would assert on the presence of a SearchPage object.
    }
}
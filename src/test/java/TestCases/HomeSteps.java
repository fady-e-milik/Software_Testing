package TestCases;

import org.group5.Pages.LoginPage;
import org.group5.Pages.RegisterPage;
import org.openqa.selenium.WebDriver;
import org.group5.BaseClass;
import java.time.Duration;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.testng.Assert;

import org.group5.Pages.HomePage;

public class HomeSteps {

    private WebDriver driver;
    private HomePage homePage;
    private LoginPage loginPage;
    private RegisterPage registerPage;

    public HomeSteps() {
        // Avoid initializing driver here as Hooks run later; initialize in @Before
    }

    @Before(order = 1)
    public void init() {
        this.driver = BaseClass.getDriver();
        this.homePage = new HomePage(this.driver);
        this.loginPage = new LoginPage(this.driver); // initialize LoginPage to avoid NPE
        this.registerPage = new RegisterPage(this.driver); // initialize RegisterPage for fallback registration
    }

    // --- GIVEN Steps ---

    @Given("I am on the OpenCart home page")
    public void i_am_on_the_opencart_home_page() {
        // Navigates to the base URL of the demo site
        driver.get(BaseClass.getBaseUrl());
        // Navigated to base URL and wait for site load
        BaseClass.waitForSiteToLoad(driver, Duration.ofSeconds(30));
        boolean isDisplayed = homePage.isHomePageDisplayed();
        Assert.assertTrue(isDisplayed,
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
        String currentUrl = driver.getCurrentUrl();
        boolean isOnLoginPage = currentUrl != null && currentUrl.contains("route=account/login");
        Assert.assertTrue(isOnLoginPage,
                "Failure: Expected to be on login page but URL was " + currentUrl);
    }

    @Then("I should be successfully navigated to the Registration Page")
    public void i_should_be_successfully_navigated_to_the_registration_page() {
        String currentUrl = driver.getCurrentUrl();
        boolean isOnRegPage = currentUrl != null && currentUrl.contains("route=account/register");
        Assert.assertTrue(isOnRegPage,
                "Failure: Expected to be on registration page but URL was " + currentUrl);
    }

    @Then("I should be navigated to the search results page for {string}")
    public void i_should_be_navigated_to_the_search_results_page(String product) {
        String currentUrl = driver.getCurrentUrl();
        boolean isOnSearchPage = currentUrl != null && currentUrl.contains("search=" + product);
        Assert.assertTrue(isOnSearchPage,
                "Failure: Did not navigate to the search results page for: " + product);
    }

    // --- Background Steps ---

    @Given("User is on Home page")
    public void user_is_on_home_page() {
        driver.get(BaseClass.getBaseUrl());
        BaseClass.waitForSiteToLoad(driver, Duration.ofSeconds(30));
        Assert.assertTrue(homePage.isHomePageDisplayed(),
                "Verification Failure: Home page is not displayed");
    }

    @Given("User has already logged in with valid credentials")
    public void user_has_already_logged_in() {
        // Check if user is already logged in
        String currentUrl = driver.getCurrentUrl();
        if (! (currentUrl != null && currentUrl.contains("route=account") && !currentUrl.contains("route=account/login"))) {
            // Not logged in, proceed with login attempt
            String email = TestContext.getEmail();
            String password = TestContext.getPassword();

            // If no credentials in context, try a default (login will likely fail and trigger registration fallback)
            if (email == null || password == null) {
                email = "test@test.com";
                password = "Test123!";
            }

            driver.get(BaseClass.getBaseUrl() + "index.php?route=account/login");
            BaseClass.waitForSiteToLoad(driver, Duration.ofSeconds(15));

            loginPage.enterCredentials(email, password);
            loginPage.clickLoginButton();
            BaseClass.waitForSiteToLoad(driver, Duration.ofSeconds(10));

            // If login succeeded, store credentials in TestContext and return
            try {
                if (loginPage.isLoginSuccessful()) {
                    TestContext.setEmail(email);
                    TestContext.setPassword(password);
                    return;
                }
            } catch (Exception ignore) {}

            // If login failed, attempt to register a new unique account (so tests can proceed)
            try {
                // Generate unique credentials and register
                String generatedEmail = "autouser+" + System.currentTimeMillis() + "@example.com";
                String generatedPassword = "Password123";

                driver.get(BaseClass.getBaseUrl() + "index.php?route=account/register");
                BaseClass.waitForSiteToLoad(driver, Duration.ofSeconds(10));

                registerPage.enterFirstName("Auto");
                registerPage.enterLastName("User");
                registerPage.enterEmail(generatedEmail);
                registerPage.enterPassword(generatedPassword);
                registerPage.checkPrivacyPolicy();
                registerPage.clickContinue();

                // Verify creation and store credentials
                String successHeader = registerPage.verifySuccessMessage();
                if (successHeader == null || !successHeader.contains("Your Account")) {
                    try { BaseClass.saveDebugArtifacts(driver, "registration_unexpected_result"); } catch (Exception ignore) {}
                    throw new RuntimeException("Registration did not complete as expected. Header: " + successHeader);
                }
                 // Store generated creds for later steps
                 TestContext.setEmail(generatedEmail);
                 TestContext.setPassword(generatedPassword);

                // Optionally verify by attempting login with newly created account
                driver.get(BaseClass.getBaseUrl() + "index.php?route=account/login");
                BaseClass.waitForSiteToLoad(driver, Duration.ofSeconds(8));
                loginPage.enterCredentials(generatedEmail, generatedPassword);
                loginPage.clickLoginButton();
                BaseClass.waitForSiteToLoad(driver, Duration.ofSeconds(8));

                if (!loginPage.isLoginSuccessful()) {
                    try { BaseClass.saveDebugArtifacts(driver, "register_then_login_failed"); } catch (Exception ignore) {}
                    throw new RuntimeException("Registration succeeded but login with new credentials failed. Check site behavior.");
                }

            } catch (Exception e) {
                // If registration fails, save artifacts and throw
                try { BaseClass.saveDebugArtifacts(driver, "registration_fallback_failed"); } catch (Exception ignore) {}
                throw new RuntimeException("Failed to ensure logged-in state: " + e.getMessage(), e);
            }

        }
    }
}
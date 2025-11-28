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

import org.group5.Pages.LoginPage;

public class LoginSteps {

    // Using BaseClass to retrieve the shared WebDriver instance initialized in Hooks
    private WebDriver driver;
    private LoginPage loginPage;

    public LoginSteps() {
        // Step instance is created before Hooks. Initialize in @Before.
    }

    @Before(order = 1)
    public void init() {
        this.driver = BaseClass.getDriver();
        this.loginPage = new LoginPage(driver);
    }

    // --- GIVEN Steps ---

    @Given("I am on the OpenCart login page")
    public void i_am_on_the_opencart_login_page() {
        // Navigate to the login URL (should ideally come from a config/property file)
        driver.get(BaseClass.getBaseUrl() + "index.php?route=account/login");
        BaseClass.waitForSiteToLoad(driver, Duration.ofSeconds(15));
        Assert.assertTrue(loginPage.isLoginPageDisplayed(),
                "Verification Failure: OpenCart Login page is not displayed.");
    }

    // --- WHEN Steps ---

    @When("I enter valid credentials with email {string} and password {string}")
    public void i_enter_valid_credentials_with_email_and_password(String email, String password) {
        loginPage.enterCredentials(email, password);
    }

    @When("I enter invalid credentials with email {string} and password {string}")
    public void i_enter_invalid_credentials_with_email_and_password(String email, String password) {
        loginPage.enterCredentials(email, password);
    }

    // --- AND Steps ---

    @And("I click the Login button")
    public void i_click_the_login_button() {
        loginPage.clickLoginButton();
    }

    @And("I click the Forgotten Password link")
    public void i_click_the_forgotten_password_link() {
        loginPage.clickForgotPasswdButton();
    }

    // --- THEN Steps ---

    @Then("I should be successfully logged in")
    public void i_should_be_successfully_logged_in() {
        Assert.assertTrue(loginPage.isLoginSuccessful(),
                "Failure: Expected to be logged in but 'My Account' header was not found.");
    }

    @Then("I should see a login failure warning message")
    public void i_should_see_a_login_failure_warning_message() {
        Assert.assertTrue(loginPage.isLoginErrorDisplayed(),
                "Failure: Expected to see a login error message but none was displayed.");
    }
}
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

import org.group5.Pages.RegisterPage;


public class RegistrationSteps {

    // Using BaseClass to retrieve the shared WebDriver instance initialized in Hooks
    private WebDriver driver;
    private RegisterPage registrationPage;

    public RegistrationSteps() {
        // We avoid initializing a driver here — Cucumber constructs Step instances before Hooks run.
    }

    @Before(order = 1)
    public void init() {
        this.driver = BaseClass.getDriver();
        this.registrationPage = new RegisterPage(this.driver);
    }

    // --- GIVEN Steps ---

    @Given("I am on the OpenCart registration page")
    public void i_am_on_the_opencart_registration_page() {
        // Navigate to the registration URL
        driver.get(BaseClass.getBaseUrl() + "index.php?route=account/register");
        BaseClass.waitForSiteToLoad(driver, Duration.ofSeconds(15));

        // Assert that we are on the correct page by checking the title
        String expectedTitle = "Register Account";
        Assert.assertTrue(driver.getTitle().contains(expectedTitle),
                "Verification: Expected title '" + expectedTitle + "' not found on page. Actual title: " + driver.getTitle());
    }

    // --- WHEN Steps (Comprehensive Registration) ---
    // This step is best for data-driven testing (e.g., from an Excel runner)
    @When("I enter registration details: {string}, {string}, {string}, {string}, {string}, {string}")
    public void i_enter_registration_details(
            String firstName,
            String lastName,
            String email,
            String telephone,
            String password,
            String confirmPassword
    ) {
        // Mapping the arguments to the Page Object methods
        registrationPage.enterFirstName(firstName);
        registrationPage.enterLastName(lastName);
        registrationPage.enterEmail(email);
        registrationPage.enterPassword(password);
    }

    // --- Separate Steps for Partial Details (Used if feature file breaks down the steps) ---
    // Note: The original 'i_enter_personal_details' step was incorrect in its parameter count.

    @When("I enter personal details: {string}, {string}, {string}, and telephone {string}")
    public void i_enter_personal_details(String firstName, String lastName, String email, String telephone) {
        registrationPage.enterFirstName(firstName);
        registrationPage.enterLastName(lastName);
        registrationPage.enterEmail(email);
    }

    @When("I enter password details: {string} and confirm password {string}")
    public void i_enter_password_details(String password, String confirmPassword) {
        registrationPage.enterPassword(password);
    }

    // Match the feature step defined in Registration.feature
    @When("I enter {string}, {string}, {string} and {string}")
    public void i_enter_4_registration_details(String firstName, String lastName, String email, String password) {
        registrationPage.enterFirstName(firstName);
        registrationPage.enterLastName(lastName);
        registrationPage.enterEmail(email);
        registrationPage.enterPassword(password);
    }

    // --- AND Steps ---

    @And("I agree to the privacy policy")
    public void i_agree_to_the_privacy_policy() {
        registrationPage.checkPrivacyPolicy();
    }

    @And("I click the Continue button")
    public void i_click_the_continue_button() {
        registrationPage.clickContinue();
    }

    // --- THEN Steps ---

    @Then("my account should be successfully created")
    public void my_account_should_be_successfully_created() {
        // Use the verifySuccessMessage() method from the Page Object
        String successMessageHeader = registrationPage.verifySuccessMessage();
        Assert.assertTrue(successMessageHeader.contains("Your Account Has Been Created!"),
                "Failure: Account creation success message not found. Header was: " + successMessageHeader);
    }

    @Then("I should see an error message indicating mandatory fields")
    public void i_should_see_an_error_message_indicating_mandatory_fields() {
        // Assert that we failed to navigate away from the registration page
        // (meaning errors are present and submission failed).
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("route=account/register"),
                "Failure: Expected to stay on registration page (due to errors), but navigated away.");
    }
}
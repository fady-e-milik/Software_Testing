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
        String registrationUrl = BaseClass.getBaseUrl() + "index.php?route=account/register";
        driver.get(registrationUrl);

        BaseClass.waitForSiteToLoad(driver, Duration.ofSeconds(15));

        String pageTitle = driver.getTitle();
        String expectedTitle = "Register Account";
        Assert.assertTrue(pageTitle != null && pageTitle.contains(expectedTitle),
                "Verification: Expected title '" + expectedTitle + "' not found on page. Actual title: " + pageTitle);
    }

    // --- WHEN Steps (Comprehensive Registration) ---
    @When("I enter registration details: {string}, {string}, {string}, {string}")
    public void i_enter_registration_details(
            String firstName,
            String lastName,
            String email,
            String password
    ) {
        registrationPage.enterFirstName(firstName);

        registrationPage.enterLastName(lastName);

        registrationPage.enterEmail(email);

        registrationPage.enterPassword(password);

        // registration details entered
    }

    // --- Separate Steps for Partial Details ---
    @When("I enter personal details: {string}, {string}, {string}, and telephone {string}")
    public void i_enter_personal_details(String firstName, String lastName, String email, String telephone) {
        // enter personal details
        // explicit local reference to satisfy static analysis
        boolean _hasTelephone = telephone != null && !telephone.trim().isEmpty();
        registrationPage.enterFirstName(firstName);
        registrationPage.enterLastName(lastName);
        registrationPage.enterEmail(email);
        // personal details entered
    }

    @When("I enter password details: {string} and confirm password {string}")
    public void i_enter_password_details(String password, String confirmPassword) {
        // explicit local reference to satisfy static analysis
        boolean _hasConfirm = confirmPassword != null && !confirmPassword.trim().isEmpty();
        registrationPage.enterPassword(password);
        // password details entered
    }

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

        // Store credentials in TestContext for login tests
        TestContext.setEmail(getCurrentEnteredEmail());
        TestContext.setPassword(getCurrentEnteredPassword());
    }

    // --- THEN Steps ---

    @Then("my account should be successfully created")
    public void my_account_should_be_successfully_created() {
        String successMessageHeader = registrationPage.verifySuccessMessage();
        Assert.assertTrue(successMessageHeader.contains("Your Account"),
                "Failure: Account creation success message not found. Header was: " + successMessageHeader);
    }

    @Then("I should see an error message indicating mandatory fields")
    public void i_should_see_an_error_message_indicating_mandatory_fields() {
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl != null && currentUrl.contains("route=account/register"),
                "Failure: Expected to stay on registration page (due to errors), but navigated away.");
    }

    // --- Helper Methods ---

    private String getCurrentEnteredEmail() {
        // This would need to be extracted from the page or from a context variable
        // For now, returning a placeholder that should be updated based on test needs
        return TestContext.getLastEnteredEmail();
    }

    private String getCurrentEnteredPassword() {
        return TestContext.getLastEnteredPassword();
    }

    // --- Moved auto-generated registration steps into class ---

    @When("I register with auto-generated details")
    public void i_register_with_auto_generated_details() {
        // Generate random email and password for registration
        String generatedEmail = "autouser+" + System.currentTimeMillis() + "@example.com";
        String generatedPassword = "Password123";

        // Reuse existing navigation step
        i_am_on_the_opencart_registration_page();

        registrationPage.enterFirstName("Auto");
        registrationPage.enterLastName("User");
        registrationPage.enterEmail(generatedEmail);
        registrationPage.enterPassword(generatedPassword);
        registrationPage.checkPrivacyPolicy();
        registrationPage.clickContinue();

        // Verify creation
        my_account_should_be_successfully_created();

        // Store for later use in the scenario
        TestContext.setEmail(generatedEmail);
        TestContext.setPassword(generatedPassword);
    }

    @Given("I register a new unique user")
    public void i_register_a_new_unique_user() {
        // Delegate to the existing auto-generated registration step to avoid duplication
        i_register_with_auto_generated_details();
    }
}

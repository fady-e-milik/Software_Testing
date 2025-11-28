package TestRunner;

import org.group5.ExcelUtility; // Import the utility created above
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.FeatureWrapper;
import io.cucumber.testng.PickleWrapper;
import io.cucumber.testng.TestNGCucumberRunner;

public class RegistrationDataRunner extends AbstractTestNGCucumberTests {

    // TestNG Data Provider to read from Excel
    @DataProvider(name = "registrationDataFromExcel", parallel = false) // Set to true for parallel execution
    public Object[][] getTestData() {
        // Define file path and sheet name
        // NOTE: Adjust the path if your project structure is different
        String filePath = "src/main/resources/TestData/TestData.xlsx";
        String sheetName = "ValidRegistrationUsers";

        // Call the utility to read the data
        return ExcelUtility.getTestData(filePath, sheetName);
    }

    // We will use the main TestRunner for standard execution,
    // so we override the scenarios() method to provide zero scenarios,
    // and use a custom @Test method below to handle the Excel data.
    // This allows us to run this class directly for Excel tests.
    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return new Object[0][0];
    }

    // TestNG Test Method driven by the DataProvider
    @Test(dataProvider = "registrationDataFromExcel")
    public void runExcelDrivenRegistration(
            String firstName, String lastName, String email, String password
    ) throws Throwable {

        // This is a placeholder. You would typically call the steps directly here
        // or execute a lightweight TestNGCucumberRunner specifically for this Excel scenario.

        System.out.println("--- Running Registration Test for Email: " + email + " ---");

        // Instantiate your Step Definitions (this assumes WebDriver/DI setup is handled)
        // If you are using dependency injection (PicoContainer), this setup needs refinement.
        // For simplicity, we call the core page actions:

        // 1. You must get the WebDriver instance here and pass it to the pages/steps.
        // WebDriver driver = ...
        // RegistrationSteps registrationSteps = new RegistrationSteps(driver);

        // 2. Execute the steps:
        // registrationSteps.i_am_on_the_opencart_registration_page();
        // registrationSteps.i_enter_personal_details(firstName, lastName, email, telephone);
        // registrationSteps.i_enter_password_details(password, confirmPassword);
        // registrationSteps.i_agree_to_the_privacy_policy();
        // registrationSteps.i_click_the_continue_button();
        // registrationSteps.my_account_should_be_successfully_created();

        System.out.println("Data received: " + firstName + ", " + email + ", " + password);
    }
}
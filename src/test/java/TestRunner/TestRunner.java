package TestRunner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

/**
 * Main Test Runner for all Cucumber features
 * Runs all scenarios from Home, Login, Registration, MyAccount, Cart, and Checkout features
 */
@CucumberOptions(
    features = "src/main/resources/Features",
    glue = {
        "TestCases",
        "Hooks"
    },
    plugin = {
        "pretty",
        "html:target/cucumber-reports/cucumber.html",
        "json:target/cucumber-reports/cucumber.json"
    },
    monochrome = false,
    dryRun = false,
    publish = false
)
public class TestRunner extends AbstractTestNGCucumberTests {

    /**
     * DataProvider for parallel execution control
     * Set parallel = true for concurrent test execution
     */
    @Override
    @DataProvider(parallel = false)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
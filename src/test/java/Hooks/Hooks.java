package Hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.group5.BaseClass;

import java.time.Duration;


public class Hooks {

    /**
     * Runs before every scenario. Initializes the driver.
     */
    @Before(order = 0) // Order ensures this runs first
    public void setup(Scenario scenario) {
        scenario.log("Setting up test scenario: " + scenario.getName());

        // Initialize browser. Reads browser from system property 'browser' when provided.
        // Example: -Dbrowser=chrome or -Dbrowser=edge
        String browser = System.getProperty("browser", "chrome");
        scenario.log("Browser: " + browser);

        BaseClass.initializeDriver(browser);
        scenario.log("Driver initialized successfully");
    }

    /**
     * Runs after every scenario. Quits the driver.
     */
    @After(order = 1)
    public void tearDown(Scenario scenario) {
        boolean passed = !scenario.isFailed();
        scenario.log("Scenario ended. Passed: " + passed);

        try {
            if (!passed) {
                // save screenshot, HTML, console logs to target/debug-artifacts
                scenario.log("Scenario failed - saving debug artifacts");
                String safeName = scenario.getName().replaceAll("[^a-zA-Z0-9-_]", "_");
                BaseClass.saveDebugArtifacts(BaseClass.getDriver(), "scenarioFailure_" + safeName);
            }
        } catch (Exception e) {
            scenario.log("Failed to save debug artifacts: " + e.getMessage());
        } finally {
            scenario.log("Closing WebDriver");
            BaseClass.quitDriver();
            scenario.log("Test scenario completed");
        }
    }

    // Hook to navigate to the base URL for relevant scenarios
    @Before("@Home or @Registration or @Login")
    public void navigateToHomePage(Scenario scenario) {
        scenario.log("Navigation hook triggered for scenario: " + scenario.getName());
        scenario.log("Navigating to base URL: " + BaseClass.getBaseUrl());

        BaseClass.getDriver().get(BaseClass.getBaseUrl());

        // Wait for initial site load (handles Cloudflare/JS interstitials)
        scenario.log("Waiting for site to load...");
        BaseClass.waitForSiteToLoad(BaseClass.getDriver(), Duration.ofSeconds(30));
        scenario.log("Site loaded successfully");
    }
}
package Hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.group5.BaseClass;

import java.time.Duration;


public class Hooks {

    // NOTE: This simple setup requires you to use the DriverFactory.getDriver()
    // in your Step Definition classes to retrieve the initialized driver.

    /**
     * Runs before every scenario. Initializes the driver.
     */
    @Before(order = 0) // Order ensures this runs first
    public void setup() {
        // Initialize browser. Reads browser from system property 'browser' when provided.
        // Example: -Dbrowser=chrome or -Dbrowser=edge
        String browser = System.getProperty("browser", "chrome");
        BaseClass.initializeDriver(browser);
    }

    /**
     * Runs after every scenario. Quits the driver.
     */
    @After(order = 0) // Order ensures this runs last
    public void tearDown() {
        BaseClass.quitDriver();
    }

    /**
     * Runs after every scenario. If the scenario failed, save debug artifacts.
     */
    @After(order = 1) // Order ensures this runs after the default tearDown
    public void tearDown(Scenario scenario) {
        try {
            if (scenario != null && scenario.isFailed()) {
                // save screenshot, HTML, console logs to target/debug-artifacts
                String safeName = scenario.getName().replaceAll("[^a-zA-Z0-9-_]", "_");
                BaseClass.saveDebugArtifacts(BaseClass.getDriver(), "scenarioFailure_" + safeName);
            }
        } catch (Exception ignored) {
        } finally {
            BaseClass.quitDriver();
        }
    }

    // Optional: Add a hook to navigate to the base URL
    @Before("@Home or @Registration or @Login")
    public void navigateToHomePage() {
        BaseClass.getDriver().get(BaseClass.getBaseUrl());
        // Wait for initial site load (handles Cloudflare/JS interstitials)
        BaseClass.waitForSiteToLoad(BaseClass.getDriver(), Duration.ofSeconds(30));

    }
}
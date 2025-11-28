package Hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.group5.BaseClass;
import java.time.Duration;
import org.openqa.selenium.WebDriver;

/**
 * Cucumber Hooks class to manage the WebDriver lifecycle.
 * Assumes a Dependency Injection framework (like PicoContainer) is used
 * to share the WebDriver instance across steps.
 */
public class Hooks {

    private static WebDriver driver;

    // NOTE: This simple setup requires you to use the DriverFactory.getDriver()
    // in your Step Definition classes to retrieve the initialized driver.

    /**
     * Runs before every scenario. Initializes the driver.
     */
    @Before(order = 0) // Order ensures this runs first
    public void setup() {
        // Initialize browser. Reads browser from system property 'browser' when provided.
        // Example: -Dbrowser=chrome or -Dbrowser=edge
        String browser = System.getProperty("browser", "edge");
        driver = BaseClass.initializeDriver(browser);
    }

    /**
     * Runs after every scenario. Quits the driver.
     */
    @After(order = 0) // Order ensures this runs last
    public void tearDown() {
        BaseClass.quitDriver();
    }

    // Optional: Add a hook to navigate to the base URL
    @Before("@Home or @Registration or @Login")
    public void navigateToHomePage() {
        BaseClass.getDriver().get(BaseClass.getBaseUrl());
        // Wait for initial site load (handles Cloudflare/JS interstitials)
        BaseClass.waitForSiteToLoad(BaseClass.getDriver(), Duration.ofSeconds(30));
    }
}
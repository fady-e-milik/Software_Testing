package org.group5;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import java.time.Duration;

public class BaseClass {

    // Use ThreadLocal for thread-safe WebDriver instances when running in parallel
    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    private static final String BASE_URL = "https://demo.opencart.com/en-gb?route=common/home"; // OpenCart Demo URL

    public static WebDriver initializeDriver(String browserType) {
        if (browserType.equalsIgnoreCase("chrome")) {
            try {
                WebDriverManager.chromedriver().setup();
            } catch (Exception e) {
                System.err.println("WebDriverManager failed to download ChromeDriver. Ensure ChromeDriver is installed and available in PATH or set webdriver.chrome.driver system property.");
            }
            driver.set(new ChromeDriver());
        } else if (browserType.equalsIgnoreCase("edge")) {
            // Using EdgeDriver as requested
            try {
                WebDriverManager.edgedriver().setup();
            } catch (Exception e) {
                System.err.println("WebDriverManager failed to download EdgeDriver. Ensure EdgeDriver is installed and available in PATH or set webdriver.edge.driver system property.");
            }
            driver.set(new EdgeDriver());
        } else {
            // Default to Chrome if argument is invalid
            System.err.println("Invalid browser type specified. Defaulting to Chrome.");
            try {
                WebDriverManager.chromedriver().setup();
            } catch (Exception e) {
                System.err.println("WebDriverManager failed to download ChromeDriver. Ensure ChromeDriver is installed and available in PATH or set webdriver.chrome.driver system property.");
            }
            driver.set(new ChromeDriver());
        }

        // Apply global settings
        WebDriver d = driver.get();
        d.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        d.manage().window().maximize();

        return d;
    }

    public static WebDriver getDriver() {
        WebDriver d = driver.get();
        if (d == null) {
            throw new IllegalStateException("WebDriver is not initialized. Call initializeDriver() first.");
        }
        return d;
    }

    public static void waitForSiteToLoad(WebDriver driver, Duration timeout) {
        WebDriverWait wait = new WebDriverWait(driver, timeout);

        ExpectedCondition<Boolean> siteReady = d -> {
            try {
                String title = d.getTitle();
                if (title != null && title.toLowerCase().contains("just a moment")) {
                    return false; // still interstitial
                }
                // A good sign the site loaded is presence of the homepage main slider
                try {
                    return d.findElement(By.id("slideshow0")).isDisplayed();
                } catch (Exception e) {
                    // If not found, fall back to checking body text exists and title doesn't indicate an interstitial
                    return title != null && !title.trim().isEmpty();
                }
            } catch (Exception e) {
                return false;
            }
        };

        try {
            wait.until(siteReady);
        } catch (Exception e) {
            // No-op: The caller should handle the case where the site did not become ready in time
            System.err.println("Warning: The site did not become ready within " + timeout.getSeconds() + " seconds."
                    + " Current title: " + (driver.getTitle() == null ? "(no title)" : driver.getTitle()));
        }
    }

    public static void quitDriver() {
        WebDriver d = driver.get();
        if (d != null) {
            d.quit();
            driver.remove();
        }
    }

    public static String getBaseUrl() {
        return BASE_URL;
    }
}
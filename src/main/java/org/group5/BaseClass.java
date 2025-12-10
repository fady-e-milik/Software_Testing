// src/main/java/org/group5/BaseClass.java
package org.group5;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class BaseClass {

    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    private static final String BASE_URL = "http://localhost/opencart/upload/";
    private static final String STEALTH_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";

    private static final Duration DEFAULT_WAIT = Duration.ofSeconds(15);
    private static final Duration DEFAULT_POLL = Duration.ofMillis(500);

    public static WebDriver initializeDriver(String browserType) {
        WebDriver d;
        if (browserType == null || browserType.equalsIgnoreCase("chrome")) {
            try { WebDriverManager.chromedriver().setup(); } catch (Exception ignored) {}
            ChromeOptions opts = new ChromeOptions();
            opts.addArguments("--disable-blink-features=AutomationControlled");
            opts.addArguments("--disable-extensions");
            opts.addArguments("--disable-infobars");
            opts.addArguments("user-agent=" + STEALTH_USER_AGENT);
            d = new ChromeDriver(opts);
        } else if (browserType.equalsIgnoreCase("edge")) {
            try { WebDriverManager.edgedriver().setup(); } catch (Exception ignored) {}
            d = new EdgeDriver();
        } else {
            // default to chrome
            try { WebDriverManager.chromedriver().setup(); } catch (Exception ignored) {}
            ChromeOptions opts = new ChromeOptions();
            opts.addArguments("user-agent=" + STEALTH_USER_AGENT);
            d = new ChromeDriver(opts);
        }

        d.manage().timeouts().implicitlyWait(Duration.ofSeconds(0)); // prefer explicit waits
        d.manage().window().maximize();
        driver.set(d);
        return d;
    }

    public static WebDriver getDriver() {
        WebDriver d = driver.get();
        if (d == null) throw new IllegalStateException("WebDriver is not initialized.");
        return d;
    }

    public static String getBaseUrl() { return BASE_URL; }

    public static Object executeScript(String script, Object... args) {
        try { return ((JavascriptExecutor) getDriver()).executeScript(script, args); }
        catch (Exception e) { throw new RuntimeException("executeScript failed: " + e.getMessage(), e); }
    }

    // By-based waits
    public static WebElement waitForElement(By locator) {
        return waitForElement(getDriver(), locator, DEFAULT_WAIT);
    }

    public static WebElement waitForElement(By locator, Duration timeout) {
        return waitForElement(getDriver(), locator, timeout);
    }

    private static WebElement waitForElement(WebDriver d, By locator, Duration timeout) {
        FluentWait<WebDriver> wait = new FluentWait<>(d)
                .withTimeout(timeout)
                .pollingEvery(DEFAULT_POLL)
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static WebElement waitForClickable(By locator) {
        return waitForClickable(getDriver(), locator, DEFAULT_WAIT);
    }

    public static WebElement waitForClickable(By locator, Duration timeout) {
        return waitForClickable(getDriver(), locator, timeout);
    }

    private static WebElement waitForClickable(WebDriver d, By locator, Duration timeout) {
        WebDriverWait wait = new WebDriverWait(d, timeout);
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    // WebElement-based waits (PageFactory fields)
    public static WebElement waitForElement(WebElement element, Duration timeout) {
        WebDriver d = getDriver();
        WebDriverWait wait = new WebDriverWait(d, timeout);
        wait.until(driver1 -> {
            try { return element.isDisplayed(); } catch (StaleElementReferenceException | NoSuchElementException ex) { return false; }
        });
        return element;
    }

    public static WebElement waitForClickable(WebElement element, Duration timeout) {
        WebDriver d = getDriver();
        WebDriverWait wait = new WebDriverWait(d, timeout);
        wait.until(driver1 -> {
            try { return element.isDisplayed() && element.isEnabled(); } catch (StaleElementReferenceException | NoSuchElementException ex) { return false; }
        });
        return element;
    }

    public static List<WebElement> waitForElements(By locator) {
        FluentWait<WebDriver> wait = new FluentWait<>(getDriver())
                .withTimeout(DEFAULT_WAIT)
                .pollingEvery(DEFAULT_POLL)
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
    }

    public static void waitForSiteToLoad(WebDriver driver, Duration timeout) {
        WebDriverWait wait = new WebDriverWait(driver, timeout);
        wait.until(d -> {
            try {
                String title = d.getTitle();
                if (title == null || title.trim().isEmpty()) return false;
                return true;
            } catch (Exception e) { return false; }
        });
    }

    // Debug helpers
    public static void saveDebugArtifacts(WebDriver d, String prefix) {
        if (d == null) return;
        try {
            Path outDir = Paths.get("target", "debug-artifacts");
            Files.createDirectories(outDir);
            String ts = new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date());
            String base = prefix + "_" + ts;
            // screenshot
            try {
                if (d instanceof TakesScreenshot) {
                    File scr = ((TakesScreenshot) d).getScreenshotAs(OutputType.FILE);
                    Path dest = outDir.resolve(base + ".png");
                    Files.copy(scr.toPath(), dest);
                }
            } catch (Exception ignored) {}
            // html
            try {
                String html = d.getPageSource();
                Files.writeString(outDir.resolve(base + ".html"), html);
            } catch (Exception ignored) {}
            // console
            try {
                LogEntries logs = d.manage().logs().get("browser");
                StringBuilder sb = new StringBuilder();
                for (LogEntry e : logs) sb.append(new Date(e.getTimestamp())).append(" ").append(e.getLevel()).append(" ").append(e.getMessage()).append(System.lineSeparator());
                Files.writeString(outDir.resolve(base + ".console.log"), sb.toString());
            } catch (Exception ignored) {}
        } catch (IOException ignored) {}
    }

    public static void quitDriver() {
        WebDriver d = driver.get();
        if (d != null) { d.quit(); driver.remove(); }
    }
}

// end of BaseClass.java

// src/main/java/org/group5/BaseClass.java
package org.group5;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.JavascriptExecutor;
import io.github.bonigarcia.wdm.WebDriverManager;
import java.time.Duration;
import java.util.Map;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import java.util.List;

public class BaseClass {

    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    // Use site root so test code can append 'index.php?route=...'
    private static final String BASE_URL = "http://localhost/opencart/upload/";
    private static final String STEALTH_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";

    private static ChromeOptions getStealthChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("user-agent=" + STEALTH_USER_AGENT);
        options.addArguments("--disable-extensions");
        options.addArguments("disable-infobars");
        return options;
    }

    public static WebDriver initializeDriver(String browserType) {
        WebDriver d = null;

        if (browserType.equalsIgnoreCase("chrome")) {
            try {
                WebDriverManager.chromedriver().setup();
            } catch (Exception e) {
                System.err.println("WebDriverManager failed to download ChromeDriver. Check network and local setup.");
            }

            ChromeDriver chrome = new ChromeDriver(getStealthChromeOptions());
            // Apply CDP-based stealth BEFORE any navigation
            applyAdvancedStealth(chrome);
            d = chrome;

        } else if (browserType.equalsIgnoreCase("edge")) {
            try {
                WebDriverManager.edgedriver().setup();
            } catch (Exception e) {
                System.err.println("WebDriverManager failed to download EdgeDriver. Check network and local setup.");
            }
            d = new EdgeDriver();

        } else {
            System.err.println("Invalid browser type specified. Defaulting to Chrome with stealth options.");
            try {
                WebDriverManager.chromedriver().setup();
            } catch (Exception e) {
                System.err.println("WebDriverManager failed to download ChromeDriver. Check network and local setup.");
            }
            ChromeDriver chrome = new ChromeDriver(getStealthChromeOptions());
            applyAdvancedStealth(chrome);
            d = chrome;
        }

        if (d == null) {
            throw new RuntimeException("Failed to initialize WebDriver.");
        }

        // Remove late injection; CDP script is already added. Keep this as a fallback only.
        try {
            String fallback = "try{Object.defineProperty(navigator, 'webdriver', {get: () => undefined});}catch(e){}";
            ((JavascriptExecutor) d).executeScript(fallback);
        } catch (Exception e) {
            System.err.println("Error injecting fallback JavaScript for stealth: " + e.getMessage());
        }

        driver.set(d);
        d.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        d.manage().window().maximize();

        return d;
    }

    public static void applyAdvancedStealth(ChromeDriver chromeDriver) {
        // 1) Add a script that runs on every new document before any page scripts
        String stealthScript = ""
                + "(function(){"
                + "try{"
                + "  Object.defineProperty(navigator, 'webdriver', {get: () => undefined});"
                + "  Object.defineProperty(navigator, 'languages', {get: () => ['en-US','en']});"
                + "  Object.defineProperty(navigator, 'plugins', {get: () => [1,2,3,4]});"
                + "  // Spoof WebGL vendor/renderer"
                + "  try{"
                + "    const getParameter = WebGLRenderingContext.prototype.getParameter;"
                + "    WebGLRenderingContext.prototype.getParameter = function(param) {"
                + "      if(param === 37445) return 'Google Inc.';"    // UNMASKED_VENDOR_WEBGL
                + "      if(param === 37446) return 'NVIDIA GeForce GTX 1650 Ti';" // UNMASKED_RENDERER_WEBGL"
                + "      return getParameter.call(this, param);"
                + "    };"
                + "  }catch(e){}"
                + "}catch(e){}"
                + "})();";

        // Inject the script to run before any page scripts
        chromeDriver.executeCdpCommand("Page.addScriptToEvaluateOnNewDocument", Map.of("source", stealthScript));

        // 2) Set a realistic user agent via CDP
        chromeDriver.executeCdpCommand("Network.setUserAgentOverride", Map.of("userAgent", STEALTH_USER_AGENT));

        // 3) Set locale
        chromeDriver.executeCdpCommand("Emulation.setLocaleOverride", Map.of("locale", "en-US"));

        // Note: more advanced CDP calls (geolocation, permissions) can be added as needed.
    }

    public static WebDriver getDriver() {
        WebDriver d = driver.get();
        if (d == null) {
            throw new IllegalStateException("WebDriver is not initialized. Call initializeDriver() first.");
        }
        return d;
    }

    // Smart wait defaults
    private static final Duration DEFAULT_WAIT = Duration.ofSeconds(15);
    private static final Duration DEFAULT_POLL = Duration.ofMillis(500);

    /**
     * Wait for a single element by locator. Uses a FluentWait that ignores common transient exceptions.
     * @param d driver to use
     * @param locator By locator
     * @param timeout how long to wait
     * @param polling polling interval
     * @param requireVisible if true waits for visibility, otherwise presence
     * @return the found WebElement
     */
    public static WebElement waitForElement(WebDriver d, By locator, Duration timeout, Duration polling, boolean requireVisible) {
        if (d == null) throw new IllegalArgumentException("WebDriver must not be null");
        Wait<WebDriver> wait = new FluentWait<>(d)
                .withTimeout(timeout)
                .pollingEvery(polling)
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class)
                .ignoring(ElementNotInteractableException.class)
                .ignoring(WebDriverException.class);
        if (requireVisible) {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        } else {
            return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        }
    }

    public static List<WebElement> waitForElements(WebDriver d, By locator, Duration timeout, Duration polling) {
        if (d == null) throw new IllegalArgumentException("WebDriver must not be null");
        Wait<WebDriver> wait = new FluentWait<>(d)
                .withTimeout(timeout)
                .pollingEvery(polling)
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class)
                .ignoring(WebDriverException.class);
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
    }

    public static WebElement waitForClickable(WebDriver d, By locator, Duration timeout, Duration polling) {
        if (d == null) throw new IllegalArgumentException("WebDriver must not be null");
        Wait<WebDriver> wait = new FluentWait<>(d)
                .withTimeout(timeout)
                .pollingEvery(polling)
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class)
                .ignoring(WebDriverException.class);
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    // Convenience overloads using the thread-local driver and sensible defaults
    public static WebElement waitForElement(By locator) {
        return waitForElement(getDriver(), locator, DEFAULT_WAIT, DEFAULT_POLL, true);
    }

    public static WebElement waitForElement(By locator, Duration timeout) {
        return waitForElement(getDriver(), locator, timeout, DEFAULT_POLL, true);
    }

    public static List<WebElement> waitForElements(By locator) {
        return waitForElements(getDriver(), locator, DEFAULT_WAIT, DEFAULT_POLL);
    }

    public static WebElement waitForClickable(By locator) {
        return waitForClickable(getDriver(), locator, DEFAULT_WAIT, DEFAULT_POLL);
    }

    public static void waitForSiteToLoad(WebDriver driver, Duration timeout) {
        WebDriverWait wait = new WebDriverWait(driver, timeout);

        ExpectedCondition<Boolean> siteReady = d -> {
            // defensive null-check: WebDriver passed to the condition may occasionally be null
            if (d == null) {
                return false;
            }
            try {
                String title = d.getTitle();
                if (title != null && title.toLowerCase().contains("just a moment")) {
                    return false;
                }
                try {
                    return d.findElement(By.cssSelector("#logo a")).isDisplayed();
                } catch (Exception e) {
                    return title != null && !title.trim().isEmpty();
                }
            } catch (Exception e) {
                return false;
            }
        };

        try {
            wait.until(siteReady);
        } catch (Exception e) {
            // Make the diagnostic message null-safe (driver or title might be null)
            String currentTitle;
            try {
                currentTitle = (driver == null || driver.getTitle() == null) ? "(no title)" : driver.getTitle();
            } catch (Exception ex) {
                currentTitle = "(no title)";
            }
            System.err.println("Warning: The site did not become ready within " + timeout.getSeconds() + " seconds."
                    + " Current title: " + currentTitle);

            // Save debug artifacts so you can inspect the challenge page (screenshot + HTML)
            try {
                saveDebugArtifacts(driver, "siteLoadTimeout");
            } catch (Exception ex) {
                System.err.println("Failed to save debug artifacts: " + ex.getMessage());
            }
        }
    }

    // New helper: save screenshot and page HTML into target/debug-artifacts
    public static void saveDebugArtifacts(WebDriver driver, String prefix) {
        if (driver == null) return;
        try {
            Path outDir = Paths.get("target", "debug-artifacts");
            Files.createDirectories(outDir);

            String ts = new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date());
            String baseName = prefix + "_" + ts;

            // screenshot
            try {
                if (driver instanceof TakesScreenshot) {
                    File scr = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                    Path dest = outDir.resolve(baseName + ".png");
                    Files.copy(scr.toPath(), dest);
                    System.err.println("Saved screenshot to: " + dest.toAbsolutePath());
                }
            } catch (Exception e) {
                System.err.println("Screenshot capture failed: " + e.getMessage());
            }

            // page HTML
            try {
                String page = driver.getPageSource();
                if (page != null) {
                    Path html = outDir.resolve(baseName + ".html");
                    Files.writeString(html, page);
                    System.err.println("Saved page HTML to: " + html.toAbsolutePath());
                } else {
                    System.err.println("Page source was null; skipping HTML save.");
                }
            } catch (Exception e) {
                System.err.println("Saving page HTML failed: " + e.getMessage());
            }

            // browser console logs (best-effort)
            try {
                LogEntries logs = driver.manage().logs().get("browser");
                if (logs != null) {
                    Path logFile = outDir.resolve(baseName + ".console.log");
                    StringBuilder sb = new StringBuilder();
                    for (LogEntry entry : logs) {
                        sb.append(new Date(entry.getTimestamp())).append(" ").append(entry.getLevel()).append(" ")
                                .append(entry.getMessage()).append(System.lineSeparator());
                    }
                    Files.writeString(logFile, sb.toString());
                    System.err.println("Saved console logs to: " + logFile.toAbsolutePath());
                }
            } catch (Exception e) {
                System.err.println("Could not collect console logs: " + e.getMessage());
            }

        } catch (IOException ioe) {
            System.err.println("Could not create debug-artifacts directory: " + ioe.getMessage());
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

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
import org.openqa.selenium.chromium.ChromiumDriver;
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

public class BaseClass {

    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    private static final String BASE_URL = "https://demo.opencart.com/";
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
            EdgeDriver edge = new EdgeDriver();
            applyAdvancedStealth(edge);
            d = edge;

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

        System.err.println("Initialized WebDriver implementation: " + d.getClass().getName());

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

    // Updated: accept any WebDriver but only run CDP for ChromiumDriver instances
    public static void applyAdvancedStealth(WebDriver wd) {
        if (wd == null) return;
        if (!(wd instanceof ChromiumDriver)) {
            // not a Chromium-based driver; nothing to do
            return;
        }
        ChromiumDriver chromeDriver = (ChromiumDriver) wd;

        String stealthScript = "(function(){"
                + "try{"
                + "  // Basic webdriver and language spoofing\\n"
                + "  Object.defineProperty(navigator, 'webdriver', {get: () => undefined});"
                + "  Object.defineProperty(navigator, 'languages', {get: () => ['en-US','en']});"
                + "  Object.defineProperty(navigator, 'plugins', {get: () => [1,2,3,4]});"
                + "  // Additional small fingerprints\\n"
                + "  Object.defineProperty(navigator, 'hardwareConcurrency', {get: () => 8});"
                + "  try{ Object.defineProperty(navigator, 'deviceMemory', {get: () => 8}); }catch(e){}"
                + "  try{ Object.defineProperty(navigator, 'platform', {get: () => 'Win32'}); }catch(e){}"
                + "  // Provide a minimal window.chrome object to mimic real Chrome\\n"
                + "  try{ window.chrome = window.chrome || { runtime: {}, webstore: {} }; }catch(e){}"
                + "  // Spoof userAgentData (modern browsers)\\n"
                + "  try{"
                + "    if(!navigator.userAgentData) {"
                + "      Object.defineProperty(navigator, 'userAgentData', {get: () => ({brands:[{brand:'Chromium',version:'120'},{brand:'Google Chrome',version:'120'}], mobile: false, getHighEntropyValues: (hints) => Promise.resolve({ architecture: 'x86', model: '', platform: 'Windows', platformVersion: '10.0.0', uaFullVersion: '120.0.0.0' }) })});"
                + "    } else {"
                + "      try{ navigator.userAgentData.brands = [{brand:'Chromium',version:'120'},{brand:'Google Chrome',version:'120'}]; }catch(e){}"
                + "    }"
                + "  }catch(e){}"
                + "  // Spoof WebGL vendor/renderer\\n"
                + "  try{"
                + "    const getParameter = WebGLRenderingContext.prototype.getParameter;"
                + "    WebGLRenderingContext.prototype.getParameter = function(param) {"
                + "      if(param === 37445) return 'Google Inc.';"    // UNMASKED_VENDOR_WEBGL
                + "      if(param === 37446) return 'NVIDIA GeForce GTX 1650 Ti';" // UNMASKED_RENDERER_WEBGL"
                + "      return getParameter.call(this, param);"
                + "    };"
                + "  }catch(e){}"
                + "  // Permissions query fallback\\n"
                + "  try{"
                + "    const origQuery = navigator.permissions.query;"
                + "    navigator.permissions.query = function(query) {"
                + "      if(query && query.name === 'notifications') { return Promise.resolve({state: Notification.permission}); }"
                + "      return origQuery(query);"
                + "    };"
                + "  }catch(e){}"
                + "}catch(e){}"
                + "})();";

        // Inject the script to run before any page scripts
        try {
            chromeDriver.executeCdpCommand("Page.addScriptToEvaluateOnNewDocument", Map.of("source", stealthScript));
        } catch (Exception e) {
            System.err.println("CDP 'Page.addScriptToEvaluateOnNewDocument' failed: " + e.getMessage());
        }

        // 2) Set a realistic user agent via CDP
        try {
            chromeDriver.executeCdpCommand("Network.setUserAgentOverride", Map.of("userAgent", STEALTH_USER_AGENT));
        } catch (Exception e) {
            System.err.println("CDP 'Network.setUserAgentOverride' failed: " + e.getMessage());
        }

        // 3) Set locale
        try {
            chromeDriver.executeCdpCommand("Emulation.setLocaleOverride", Map.of("locale", "en-US"));
        } catch (Exception e) {
            System.err.println("CDP 'Emulation.setLocaleOverride' failed: " + e.getMessage());
        }

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

            // If interstitial looks like Cloudflare / verification and manualSolve is enabled, pause and let user solve
            try {
                boolean looksLikeCF = false;
                try {
                    String t = driver.getTitle();
                    if (t != null && t.toLowerCase().contains("just a moment")) looksLikeCF = true;
                    String page = driver.getPageSource();
                    if (!looksLikeCF && page != null && page.toLowerCase().contains("verifying you are human")) looksLikeCF = true;
                } catch (Exception ignore) {
                }

                boolean manual = Boolean.parseBoolean(System.getProperty("manualSolve", "false"));
                if (looksLikeCF && manual) {
                    System.err.println("Cloudflare-like interstitial detected. Saved artifacts to target/debug-artifacts.");
                    System.err.println("Please solve the interstitial manually in the opened browser, then press ENTER here to continue the test.");
                    try {
                        // Wait for user input on the console
                        byte[] buffer = new byte[1024];
                        System.in.read(buffer);
                    } catch (Exception ie) {
                        // ignore
                    }
                }
            } catch (Exception ex) {
                // ignore
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
                    System.err.println("Page source was null, skipping HTML save.");
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

            // navigator properties
            try {
                captureNavigatorProperties(driver, outDir, baseName);
            } catch (Exception e) {
                System.err.println("Failed to capture navigator properties: " + e.getMessage());
            }

        } catch (IOException ioe) {
            System.err.println("Could not create debug-artifacts directory: " + ioe.getMessage());
        }
    }

    // helper to capture navigator properties via JS and save to a JSON-like file
    private static void captureNavigatorProperties(WebDriver driver, Path outDir, String baseName) {
        if (driver == null || !(driver instanceof JavascriptExecutor)) return;
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            Object res = js.executeScript(
                    "return {webdriver: navigator.webdriver, userAgent: navigator.userAgent, platform: navigator.platform, languages: navigator.languages, plugins: navigator.plugins ? navigator.plugins.length : 0, hardwareConcurrency: navigator.hardwareConcurrency, deviceMemory: navigator.deviceMemory};"
            );
            Path navFile = outDir.resolve(baseName + ".nav.json");
            Files.writeString(navFile, String.valueOf(res));
            System.err.println("Saved navigator properties to: " + navFile.toAbsolutePath());
        } catch (Exception e) {
            System.err.println("Could not capture navigator properties: " + e.getMessage());
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

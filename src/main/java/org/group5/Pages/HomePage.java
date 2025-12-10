package org.group5.Pages;

import org.group5.BaseClass;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.NoSuchElementException;
import java.time.Duration;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.Keys;

public class HomePage extends BaseClass {

    private final WebDriver driver;

    public HomePage (WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    // --- Web Elements Declaration (Using robust selectors for localhost OpenCart) ---

    // Top Navigation Links
    @FindBy(xpath = "//span[contains(text(), 'My Account')]")
    private WebElement myAccountMenu;

    @FindBy(linkText = "Register")
    private WebElement registerLink;

    @FindBy(linkText = "Login")
    private WebElement loginLink;

    // Search Bar
    @FindBy(name = "search")
    private WebElement searchInput;

    @FindBy(css = "button.btn-default[aria-label='Search']")
    private WebElement searchButton;

    @FindBy(css = "button.btn:nth-of-type(1)")
    private WebElement searchButtonAlt;

    // Main content area for page verification
    @FindBy(id = "content")
    private WebElement mainContent;

    // Featured Products section
    @FindBy(xpath = "//*[@id=\"content\"]/div[2]/div[1]/div/div[2]/div/h4/a")
    private WebElement firstFeaturedProductLink;

    // Generic first product link (works on search results and listings)
    @FindBy(xpath = "//div[contains(@class,'product-layout')]//h4/a")
    private WebElement firstProductLink;

    // Page title verification
    @FindBy(xpath = "//h1[contains(text(), 'Your Store')]")
    private WebElement pageTitle;

    @FindBy(xpath = "//*[@id=\"button-cart\"]")
    private WebElement addToCartButton;


    // --- Action Methods ---


    public void clickAddToCart() {
        try {
            // Try multiple selectors to find the actual visible Add-to-Cart element at runtime
            java.util.List<org.openqa.selenium.By> selectors = java.util.Arrays.asList(
                    org.openqa.selenium.By.id("button-cart"),
                    org.openqa.selenium.By.cssSelector("#button-cart"),
                    org.openqa.selenium.By.cssSelector("button.btn.btn-primary"),
                    org.openqa.selenium.By.cssSelector("button.btn-primary"),
                    org.openqa.selenium.By.cssSelector("input[value='Add to Cart']"),
                    org.openqa.selenium.By.xpath("//button[contains(normalize-space(.),'Add to Cart') or contains(normalize-space(.),'Add to basket') or contains(normalize-space(.),'Add to bag')]")
            );

            org.openqa.selenium.WebElement btn = null;
            for (org.openqa.selenium.By sel : selectors) {
                try {
                    java.util.List<org.openqa.selenium.WebElement> found = driver.findElements(sel);
                    for (org.openqa.selenium.WebElement e : found) {
                        try {
                            if (e.isDisplayed() && e.isEnabled()) { btn = e; break; }
                        } catch (Exception ignore) {}
                    }
                } catch (Exception ignored) {}
                if (btn != null) break;
            }

            // Fallback to the PageFactory field
            if (btn == null) {
                try {
                    btn = BaseClass.waitForElement(addToCartButton, Duration.ofSeconds(5));
                } catch (Exception ignored) {}
            }

            if (btn == null) {
                // Save artifacts to aid debugging
                try { BaseClass.saveDebugArtifacts(driver, "addToCart_not_found"); } catch (Exception ignore) {}
                throw new RuntimeException("Add to Cart button not found on the page");
            }

            // Common overlay selectors to hide (best-effort)
            String[] overlaySelectors = new String[]{
                    ".cc_banner", ".cookie-consent", "#cookie-consent", ".cookie-banner", ".cookieNotice", ".modal-backdrop", ".overlay", "#qc-cmp2-container"
            };

            // Try to detect and remove any element that sits above the center of the button
            String blockerInfo = "(none)";
            try {
                String detectAndHide =
                        "var el = arguments[0];"
                                + "var rect = el.getBoundingClientRect();"
                                + "var cx = Math.floor(rect.left + rect.width/2); var cy = Math.floor(rect.top + rect.height/2);"
                                + "var top = document.elementFromPoint(cx, cy);"
                                + "if(!top) return null;"
                                + "if(top === el || el.contains(top)) return null;"
                                + "var info = {tag: top.tagName, id: top.id || null, cls: top.className || null, outer: top.outerHTML ? top.outerHTML.slice(0,500) : ''};"
                                + "try{ top.dataset.__selenium_blocker = 'true'; top.style.display='none'; top.style.pointerEvents='none'; top.style.visibility='hidden'; top.style.zIndex='-9999'; }catch(e){};"
                                + "return info;";

                Object infoObj = ((JavascriptExecutor) driver).executeScript(detectAndHide, btn);
                if (infoObj != null) {
                    blockerInfo = infoObj.toString();
                    Thread.sleep(150);
                }
            } catch (Exception e) {
                // ignore detection failures
            }

            // Hide known overlays if present
            try {
                for (String sel : overlaySelectors) {
                    try {
                        ((JavascriptExecutor) driver).executeScript("var el = document.querySelector('" + sel + "'); if(el) el.style.display='none';");
                    } catch (Exception ignored) {}
                }
            } catch (Exception ignored) {}

            // Attempt click strategies in order
            // 1) Wait for clickable (longer) and click
            try {
                BaseClass.waitForClickable(btn, Duration.ofSeconds(15)).click();
                return;
            } catch (Exception e) { /* fallthrough */ }

            // 2) JS click (after scroll into view)
            try {
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", btn);
                Thread.sleep(150);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
                return;
            } catch (Exception e) { /* fallthrough */ }

            // 3) Actions move + click
            try {
                Actions actions = new Actions(driver);
                actions.moveToElement(btn).pause(Duration.ofMillis(150)).click().perform();
                return;
            } catch (Exception e) { /* fallthrough */ }

            // 4) If the button belongs to a form, submit the form
            try {
                ((JavascriptExecutor) driver).executeScript("var b=arguments[0]; var f=b.form || b.closest('form'); if(f){ f.submit(); } else { b.click(); }", btn);
                return;
            } catch (Exception e) { /* fallthrough */ }

            // Save artifacts then throw a detailed error
            try { BaseClass.saveDebugArtifacts(driver, "addToCart_click_failed"); } catch (Exception ignore) {}
            throw new RuntimeException("Failed to click Add to Cart button after multiple attempts. Blocking element: " + blockerInfo);

        } catch (Exception e) {
            throw new RuntimeException("Failed to click Add to Cart button: " + e.getMessage(), e);
        }
    }

    public void navigateToRegistrationPage() {
        try {
            BaseClass.waitForClickable(myAccountMenu, Duration.ofSeconds(10)).click();
            BaseClass.waitForClickable(registerLink, Duration.ofSeconds(10)).click();
        } catch (Exception e) {
            throw new RuntimeException("Failed to navigate to Registration page: " + e.getMessage(), e);
        }
    }

    public void navigateToLoginPage() {
        try {
            BaseClass.waitForClickable(myAccountMenu, Duration.ofSeconds(10)).click();
            BaseClass.waitForClickable(loginLink, Duration.ofSeconds(10)).click();
        } catch (Exception e) {
            throw new RuntimeException("Failed to navigate to Login page: " + e.getMessage(), e);
        }
    }

    public void searchForProduct(String productName) {
        try {
            WebElement searchBox = BaseClass.waitForElement(searchInput, Duration.ofSeconds(10));
            searchBox.clear();
            searchBox.sendKeys(productName);

            // Attempt to hide common overlays that may intercept clicks
            try {
                String[] overlaySelectors = new String[]{
                        ".cc_banner", ".cookie-consent", "#cookie-consent", ".cookie-banner", ".cookieNotice", ".modal-backdrop", ".overlay", "#qc-cmp2-container"
                };
                for (String sel : overlaySelectors) {
                    try {
                        ((JavascriptExecutor) driver).executeScript("var el = document.querySelector('" + sel + "'); if(el) el.style.display='none';");
                    } catch (Exception ignored) {
                        // ignore individual failures
                    }
                }
            } catch (Exception ignore) {
                // ignore
            }

            // 1) Primary clickable attempt (longer wait)
            try {
                BaseClass.waitForClickable(searchButton, Duration.ofSeconds(10)).click();
                return;
            } catch (Exception e) {
                // fallthrough to alternate attempts
            }

            // 2) Alternate button clickable
            try {
                BaseClass.waitForClickable(searchButtonAlt, Duration.ofSeconds(5)).click();
                return;
            } catch (Exception e) {
                // fallthrough
            }

            // 3) JS click on primary button
            try {
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'}); arguments[0].click();", searchButton);
                return;
            } catch (Exception e) {
                // fallthrough
            }

            // 4) JS click on alternate button
            try {
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'}); arguments[0].click();", searchButtonAlt);
                return;
            } catch (Exception e) {
                // fallthrough
            }

            // 5) Actions move + click
            try {
                Actions actions = new Actions(driver);
                actions.moveToElement(searchButton).pause(Duration.ofMillis(150)).click().perform();
                return;
            } catch (Exception e) {
                // fallthrough
            }

            // 6) Final fallback: press ENTER in the search input
            searchBox.sendKeys(Keys.ENTER);

        } catch (Exception e) {
            throw new RuntimeException("Failed to search for product " + productName + ": " + e.getMessage(), e);
        }
    }

    public boolean isProductPageDisplayed() {
        try {
            return BaseClass.waitForElement(addToCartButton, Duration.ofSeconds(10)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void clickFirstFeaturedProduct() {
        try {
            WebElement featured = BaseClass.waitForElement(firstFeaturedProductLink, Duration.ofSeconds(10));

            // If the featured product link has an href, navigate directly to it to avoid click interception
            try {
                String href = featured.getAttribute("href");
                if (href != null && !href.trim().isEmpty()) {
                    String url = href.trim();
                    if (!url.startsWith("http")) {
                        // Resolve relative URLs against base URL
                        String base = BaseClass.getBaseUrl();
                        if (!base.endsWith("/") && !url.startsWith("/")) base += "/";
                        url = base + url.replaceFirst("^/", "");
                    }
                    driver.get(url);
                    BaseClass.waitForSiteToLoad(driver, Duration.ofSeconds(10));
                    return;
                }
            } catch (Exception navEx) {
                // fallthrough to click attempts if navigation fails
            }

            // Common overlay selectors
            String[] overlaySelectors = new String[]{
                    ".cc_banner", ".cookie-consent", "#cookie-consent", ".cookie-banner", ".cookieNotice", ".modal-backdrop", ".overlay", "#qc-cmp2-container"
            };

            // Retry loop: try clicking up to 3 times, removing blockers each time
            for (int attempt = 1; attempt <= 3; attempt++) {
                // Try to hide known overlays
                try {
                    for (String sel : overlaySelectors) {
                        try {
                            ((JavascriptExecutor) driver).executeScript("var el = document.querySelector('" + sel + "'); if(el) el.style.display='none';");
                        } catch (Exception ignored) {
                        }
                    }
                } catch (Exception ignore) {
                }

                // Remove topmost element at center if blocking
                try {
                    String blockerRemover =
                            "var el = arguments[0];"
                                    + "var rect = el.getBoundingClientRect();"
                                    + "var cx = Math.floor(rect.left + rect.width/2); var cy = Math.floor(rect.top + rect.height/2);"
                                    + "var attempts = 0; var blocked = false;"
                                    + "while(attempts < 4){ var top = document.elementFromPoint(cx, cy); if(!top) break; if(top === el || el.contains(top)){ blocked=false; break;}\n"
                                    + " blocked=true; try{ top.style.display='none'; top.style.visibility='hidden'; top.style.pointerEvents='none'; top.style.zIndex='-9999'; }catch(e){}; attempts++; }"
                                    + "return blocked;";

                    Boolean wasBlocked = (Boolean) ((JavascriptExecutor) driver).executeScript(blockerRemover, featured);
                    if (wasBlocked != null && wasBlocked) Thread.sleep(150);
                } catch (Exception ignore) {
                }

                // Try normal clickable click
                try {
                    BaseClass.waitForClickable(featured, Duration.ofSeconds(8)).click();
                    return;
                } catch (Exception clickEx) {
                    // If last attempt, proceed to stronger fallbacks
                    if (attempt < 3) {
                        // small pause before retry
                        try { Thread.sleep(200); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
                        continue;
                    }
                }

                // Try JS click as fallback
                try {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'}); arguments[0].click();", featured);
                    return;
                } catch (Exception jsEx) {
                }

                // If there's an href, navigate via WebDriver.get as a strong fallback
                try {
                    String href = featured.getAttribute("href");
                    if (href != null && !href.trim().isEmpty()) {
                        String url = href.trim();
                        if (!url.startsWith("http")) {
                            String base = BaseClass.getBaseUrl();
                            if (!base.endsWith("/") && !url.startsWith("/")) base += "/";
                            url = base + url.replaceFirst("^/", "");
                        }
                        driver.get(url);
                        BaseClass.waitForSiteToLoad(driver, Duration.ofSeconds(10));
                        return;
                    }
                } catch (Exception navEx) {
                    // fallthrough
                }
            }

            // If we reach here, all attempts failed; save artifacts and throw
            try { BaseClass.saveDebugArtifacts(driver, "clickFirstFeaturedProduct_failed"); } catch (Exception ignore) {}
            throw new RuntimeException("Failed to click on the first featured product after multiple attempts.");

        } catch (Exception e) {
            throw new RuntimeException("Failed to click on the first featured product: " + e.getMessage(), e);
        }
    }

    // --- Verification Methods ---

    public boolean isOnHomePage() {
        try {
            return BaseClass.waitForElement(pageTitle, Duration.ofSeconds(10)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isHomePageDisplayed() {
        try {
            return BaseClass.waitForElement(mainContent, Duration.ofSeconds(10)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}

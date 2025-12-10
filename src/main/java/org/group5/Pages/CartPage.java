package org.group5.Pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.group5.BaseClass;
import java.time.Duration;
import java.util.List;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.JavascriptExecutor;

/**
 * Page Object Model for Shopping Cart page
 */
public class CartPage extends BaseClass {

    private final WebDriver driver;

    public CartPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(xpath = "//h1[contains(text(), 'Shopping Cart')]")
    private WebElement cartHeader;

    @FindBy(xpath = "//a[contains(@class,'btn') and contains(text(),'Checkout')]")
    private WebElement checkoutButton;

    @FindBy(css = "#output-cart > table > tbody > tr:nth-child(1) > td:nth-child(3) > form > div > a")
    private WebElement removeButton;

    @FindBy(xpath = "//input[contains(@name,'quantity')]")
    private WebElement quantityInput;

    @FindBy(css = "#content .alert")
    private WebElement cartNotification;

    @FindBy(xpath = "//td[contains(text(),'Sub-Total')]/following::td[1]")
    private WebElement subTotal;

    @FindBy(xpath = "//strong[contains(text(),'Total')]/following::td")
    private WebElement total;

    // --- Actions ---

    public void navigateToCart() {
        driver.get(BaseClass.getBaseUrl() + "index.php?route=checkout/cart");
        BaseClass.waitForSiteToLoad(driver, Duration.ofSeconds(10));
    }

    public boolean isCartPageDisplayed() {
        try {
            return BaseClass.waitForElement(cartHeader, Duration.ofSeconds(5)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void clickCheckout() {
        // Robust clicking: try multiple strategies to handle overlays or intercepted clicks
        // Try to detect and hide a topmost blocking element at the center of the checkout button
        try {
            String blockerRemover =
                "var el = arguments[0];"
                + "var rect = el.getBoundingClientRect();"
                + "var cx = Math.floor(rect.left + rect.width/2); var cy = Math.floor(rect.top + rect.height/2);"
                + "var top = document.elementFromPoint(cx, cy); if(!top) return null;"
                + "if(top === el || el.contains(top)) return null;"
                + "try{ top.dataset.__selenium_blocker = 'true'; top.style.display='none'; top.style.pointerEvents='none'; top.style.visibility='hidden'; top.style.zIndex='-9999'; }catch(e){};"
                + "return {tag: top.tagName, id: top.id || null, cls: top.className || null};";
            Object blocked = null;
            try { blocked = BaseClass.executeScript(blockerRemover, checkoutButton); } catch (Exception ignore) {}
            // small pause if we hid something
            if (blocked != null) {
                try { Thread.sleep(150); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
            }
        } catch (Exception ignore) {}

        try {
            // 1) prefer a longer clickable wait
            BaseClass.waitForClickable(checkoutButton, Duration.ofSeconds(15)).click();
            return;
        } catch (Exception e) {
            // fallthrough to other strategies
        }

        // 2) Try JS click after scrolling
        try {
            BaseClass.executeScript("arguments[0].scrollIntoView({block:'center'});", checkoutButton);
            try { Thread.sleep(200); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
            BaseClass.executeScript("arguments[0].click();", checkoutButton);
            return;
        } catch (Exception e) {
            // fallthrough
        }

        // 3) Try alternate locator (anchor/link or button) for checkout
        try {
            By alt = By.xpath("//a[contains(@href,'checkout') or contains(normalize-space(.),'Checkout') or contains(@class,'checkout')]");
            List<WebElement> altElems = driver.findElements(alt);
            for (WebElement el : altElems) {
                try {
                    if (el.isDisplayed()) {
                        try {
                            BaseClass.waitForClickable(el, Duration.ofSeconds(5)).click();
                        } catch (Exception ex) {
                            // try JS click on the element
                            try { BaseClass.executeScript("arguments[0].click();", el); } catch (Exception ignore) {}
                        }
                        return;
                    }
                } catch (Exception ignore) {}
            }
        } catch (Exception e) {
            // fallthrough
        }

        // 4) Try Actions click
        try {
            Actions actions = new Actions(driver);
            actions.moveToElement(checkoutButton).pause(Duration.ofMillis(150)).click().perform();
            return;
        } catch (Exception e) {
            // fallthrough
        }

        // 5) Last-resort: attempt to submit a form or dispatch click via JS on the closest clickable ancestor
        try {
            BaseClass.executeScript(
                "var b = arguments[0];\nvar el = b; while(el && el.tagName.toLowerCase()!== 'body'){ if(el.tagName.toLowerCase()==='a' || el.tagName.toLowerCase()==='button' || el.tagName.toLowerCase()==='input'){ el.click(); return; } el = el.parentElement;} b.click();",
                checkoutButton
            );
            return;
        } catch (Exception e) {
            // fallthrough to fail
        }

        // If we reached here, clicking failed — save diagnostics and throw
        try { BaseClass.saveDebugArtifacts(driver, "click_checkout_failed"); } catch (Exception ignore) {}
        throw new RuntimeException("Element did not become clickable: checkout button could not be clicked");
    }

    public void removeItemFromCart() {
        // Click the remove button for the first row (page-specific selector)
        try {
            BaseClass.waitForClickable(removeButton, Duration.ofSeconds(5)).click();
        } catch (Exception e) {
            // fallback: try to find any remove link in the cart rows
            try {
                List<WebElement> removes = driver.findElements(By.xpath("//a[contains(@class,'btn') and contains(@onclick,'cart.remove') or contains(@href,'remove')]") );
                if (!removes.isEmpty()) {
                    removes.get(0).click();
                }
            } catch (Exception ex) {
                throw new RuntimeException("Failed to click remove item from cart: " + ex.getMessage(), ex);
            }
        }
    }

    public void removeAllItems() {
        // Remove all items from the cart by repeatedly removing the first row until none remain
        int attempts = 0;
        while (hasCartItems() && attempts < 30) {
            try {
                removeItemFromCart();
                // give the page a moment to update
                BaseClass.waitForSiteToLoad(driver, Duration.ofSeconds(5));
                Thread.sleep(300);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                // If removal failed, break to avoid infinite loop — caller will see cart is not empty
                break;
            }
            attempts++;
        }
    }

    public void updateQuantity(int qty) {
        WebElement q = BaseClass.waitForElement(quantityInput, Duration.ofSeconds(5));
        q.clear();
        q.sendKeys(String.valueOf(qty));
        // assume update is applied by clicking update or leaving field
    }

    public boolean hasCartItems() {
        try {
            // Prefer checking the cart rows table; presence of table rows indicates items
            List<WebElement> rows = driver.findElements(By.xpath("//table[contains(@class,'table')]/tbody/tr"));
            if (rows != null && rows.size() > 0) return true;

            // Fallback: check notification text if present
            String note = getCartNotification();
            if (note == null || note.isEmpty()) return false;
            return !note.toLowerCase().contains("empty");
        } catch (Exception e) {
            return false;
        }
    }

    public String getCartNotification() {
        try {
            return BaseClass.waitForElement(cartNotification, Duration.ofSeconds(5)).getText();
        } catch (Exception e) {
            return "";
        }
    }

    public String getSubTotal() {
        try {
            return BaseClass.waitForElement(subTotal, Duration.ofSeconds(5)).getText();
        } catch (Exception e) {
            return "0.00";
        }
    }

    public String getCartTotal() {
        try {
            return BaseClass.waitForElement(total, Duration.ofSeconds(5)).getText();
        } catch (Exception e) {
            return "0.00";
        }
    }

    public boolean isCheckoutButtonVisible() {
        try {
            return BaseClass.waitForElement(checkoutButton, Duration.ofSeconds(5)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isCartEmpty() {
        try {
            String note = getCartNotification();
            return note.toLowerCase().contains("empty");
        } catch (Exception e) {
            return true;
        }
    }

}

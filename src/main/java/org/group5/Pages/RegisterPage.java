package org.group5.Pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.group5.BaseClass;
import org.openqa.selenium.WebDriver;
import java.time.Duration;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.interactions.Actions;

public class RegisterPage extends BaseClass {

    private final WebDriver driver;

    public RegisterPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    // --- Web Elements Declaration ---

    @FindBy(id = "input-firstname")
    private WebElement firstNameField;

    @FindBy(id = "input-lastname")
    private WebElement lastNameField;

    @FindBy(id = "input-email")
    private WebElement emailField;

    @FindBy(id = "input-password")
    private WebElement passwordField;

    @FindBy(xpath = "//*[@id=\"form-register\"]/div/div/input")
    private WebElement privacyPolicyCheckbox;

    @FindBy(xpath = "//*[@id=\"form-register\"]/div/button")
    private WebElement continueButton;

    @FindBy(xpath = "//h1[contains(text(), 'Your Account')]")
    private WebElement successHeader;

    @FindBy(xpath = "//h2[contains(text(), 'Personal Details')]")
    private WebElement personalDetailsSection;

    // --- Action Methods ---

    public void enterFirstName(String firstName) {
        try {
            WebElement firstNameBox = BaseClass.waitForElement(firstNameField, Duration.ofSeconds(10));
            firstNameBox.clear();
            firstNameBox.sendKeys(firstName);
        } catch (Exception e) {
            throw new RuntimeException("Failed to enter first name: " + e.getMessage(), e);
        }
    }

    public void enterLastName(String lastName) {
        try {
            WebElement lastNameBox = BaseClass.waitForElement(lastNameField, Duration.ofSeconds(10));
            lastNameBox.clear();
            lastNameBox.sendKeys(lastName);
        } catch (Exception e) {
            throw new RuntimeException("Failed to enter last name: " + e.getMessage(), e);
        }
    }

    public void enterEmail(String email) {
        try {
            WebElement emailBox = BaseClass.waitForElement(emailField, Duration.ofSeconds(10));
            emailBox.clear();
            emailBox.sendKeys(email);
        } catch (Exception e) {
            throw new RuntimeException("Failed to enter email: " + e.getMessage(), e);
        }
    }

    public void enterPassword(String password) {
        try {
            WebElement passwordBox = BaseClass.waitForElement(passwordField, Duration.ofSeconds(10));
            passwordBox.clear();
            passwordBox.sendKeys(password);
        } catch (Exception e) {
            throw new RuntimeException("Failed to enter password: " + e.getMessage(), e);
        }
    }

    public void checkPrivacyPolicy() {
        try {
            WebElement checkbox = BaseClass.waitForElement(privacyPolicyCheckbox, Duration.ofSeconds(10));

            // Attempt to hide common cookie banners/overlays that may block clicks
            try {
                String[] overlaySelectors = new String[]{
                        ".cc_banner", ".cookie-consent", "#cookie-consent", ".cookie-banner", ".cookieNotice", ".modal-backdrop", ".overlay", "#qc-cmp2-container"
                };
                for (String sel : overlaySelectors) {
                    try {
                        ((JavascriptExecutor) driver).executeScript("var el=document.querySelector('" + sel + "'); if(el) el.style.display='none';");
                    } catch (Exception ignored) {
                        // ignore failures for individual selectors
                    }
                }
            } catch (Exception ignore) {
                // ignore overlay hiding failures
            }

            // First try a normal clickable wait + click
            try {
                BaseClass.waitForClickable(checkbox, Duration.ofSeconds(5)).click();
                return;
            } catch (Exception clickEx) {
                // fallthrough to more robust attempts
            }

            // Scroll into view and try JavaScript click
            try {
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", checkbox);
                Thread.sleep(250);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", checkbox);
                return;
            } catch (Exception jsEx) {
                // fallthrough to label click attempt
            }

            // Try clicking the label associated with the checkbox
            try {
                WebElement label = driver.findElement(By.xpath("//label[@for='input-agree' or @for='agree' or contains(normalize-space(.), 'Privacy') or contains(normalize-space(.), 'agree')]"));
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", label);
                Thread.sleep(200);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", label);
                return;
            } catch (Exception lblEx) {
                // fallthrough to Actions fallback
            }

            // Final fallback: Actions moveToElement + click (native event)
            try {
                Actions actions = new Actions(driver);
                actions.moveToElement(checkbox).pause(Duration.ofMillis(150)).click().perform();
                return;
            } catch (Exception actEx) {
                // Try a JavaScript fallback to directly set the checkbox checked state
                try {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].checked = true; arguments[0].dispatchEvent(new Event('change'));", checkbox);
                    return;
                } catch (Exception jsSetEx) {
                    throw new RuntimeException("Failed to check privacy policy: all click attempts failed", jsSetEx);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to check privacy policy: " + e.getMessage(), e);
        }
    }

    public void clickContinue() {
        try {
            WebElement btn = BaseClass.waitForElement(continueButton, Duration.ofSeconds(10));

            // Attempt to hide common cookie banners/overlays that may block clicks
            try {
                String[] overlaySelectors = new String[]{
                        ".cc_banner", ".cookie-consent", "#cookie-consent", ".cookie-banner", ".cookieNotice", ".modal-backdrop", ".overlay", "#qc-cmp2-container"
                };
                for (String sel : overlaySelectors) {
                    try {
                        ((JavascriptExecutor) driver).executeScript("var el=document.querySelector('" + sel + "'); if(el) el.style.display='none';");
                    } catch (Exception ignored) {
                        // ignore individual selector failures
                    }
                }
            } catch (Exception ignore) {
                // ignore overlay hiding failures
            }

            // 1) Normal clickable click
            try {
                BaseClass.waitForClickable(btn, Duration.ofSeconds(5)).click();
                return;
            } catch (Exception e) {
                // fallthrough to next attempt
            }

            // 2) Scroll into view + JS click
            try {
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", btn);
                Thread.sleep(200);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
                return;
            } catch (Exception e) {
                // fallthrough
            }

            // 3) Actions move + click (native-like)
            try {
                Actions actions = new Actions(driver);
                actions.moveToElement(btn).pause(Duration.ofMillis(150)).click().perform();
                return;
            } catch (Exception e) {
                // fallthrough
            }

            // 4) Final fallback: try form.submit() if button is inside a form, else JS click again
            try {
                ((JavascriptExecutor) driver).executeScript("if(arguments[0].form){arguments[0].form.submit();}else{arguments[0].click();}", btn);
                return;
            } catch (Exception e) {
                throw new RuntimeException("Failed to click continue button: all click attempts failed", e);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to click continue button: " + e.getMessage(), e);
        }
    }

    // --- Verification Methods ---

    public boolean isRegistrationPageDisplayed() {
        try {
            return BaseClass.waitForElement(personalDetailsSection, Duration.ofSeconds(10)).isDisplayed();
        } catch (Exception e) {
            System.out.println("Registration page not displayed: " + e.getMessage());
            return false;
        }
    }

    public String verifySuccessMessage() {
        try {
            return BaseClass.waitForElement(successHeader, Duration.ofSeconds(10)).getText();
        } catch (Exception e) {
            System.out.println("Success message not found: " + e.getMessage());
            return "Success message not found";
        }
    }
}
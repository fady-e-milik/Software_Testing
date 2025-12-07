// src/test/java/Hooks/Hooks.java
package Hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.group5.BaseClass;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class Hooks {

    @Before(order = 0)
    public void setup() {
        String browser = System.getProperty("browser", "chrome");
        BaseClass.initializeDriver(browser);        // شغالة دلوقتي
    }

    @Before(order = 1)
    public void openHomePage() {
        WebDriver driver = BaseClass.getDriver();
        driver.get("https://demo.opencart.com/");

        // ده كل اللي محتاجه عشان يعدي Cloudflare في 2025
        new WebDriverWait(driver, Duration.ofSeconds(40))
                .until(d -> {
                    String title = d.getTitle();
                    String url = d.getCurrentUrl();
                    return !title.toLowerCase().contains("just a moment")
                            && !url.contains("challenges.cloudflare.com")
                            && d.getPageSource().contains("Your Store");
                });

        System.out.println("تم فتح OpenCart بنجاح بدون Cloudflare!");
    }

    @After
    public void tearDown() {
        BaseClass.quitDriver();
    }
}
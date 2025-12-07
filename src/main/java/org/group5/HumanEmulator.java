package org.group5;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.time.Duration;
import java.util.List;
import java.util.Random;

public class HumanEmulator {

    private static final Random random = new Random();

    public static void performHumanLikeBehavior(WebDriver driver) {
        try {
            // 1. تحريك الماوس بشكل عشوائي طبيعي
            Actions actions = new Actions(driver);
            for (int i = 0; i < 5; i++) {
                int x = 100 + random.nextInt(600);
                int y = 100 + random.nextInt(400);
                actions.moveByOffset(x, y).pause(random.nextInt(300) + 100).perform();
                actions.moveByOffset(-x / 2, -y / 2).pause(random.nextInt(200) + 50).perform();
            }

            // 2. سكرول بسيط لأعلى ولأسفل زي الإنسان
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("window.scrollBy(0, 300);");
            Thread.sleep(300 + random.nextInt(400));
            js.executeScript("window.scrollBy(0, -150);");
            Thread.sleep(200 + random.nextInt(300));
            js.executeScript("window.scrollBy(0, 200);");

            // 3. تغيير تركيز النافذة (focus) مرتين – ده بيقتل أي bot detection
            driver.switchTo().newWindow(org.openqa.selenium.WindowType.TAB);
            Thread.sleep(200);
            driver.close();
            driver.switchTo().window(driver.getWindowHandles().iterator().next());

            // 4. حركة ماوس دائرية صغيرة في النص
            actions.moveByOffset(200, 200)
                    .pause(100)
                    .moveByOffset(50, -50)
                    .pause(80)
                    .moveByOffset(-30, 40)
                    .perform();

        } catch (Exception e) {
            // ما نهتمش لو حصل error – المطلوب بس إن الحركة تبدو إنسانية
            System.out.println("HumanEmulator: بعض الحركات فشلت، لكن مفيش مشكلة – نكمل");
        }
    }

    // لو حابعتد تستخدم الدالة القديمة اللي كتبتها قبل كده
    public static void moveMouseRandomly(WebDriver driver) {
        performHumanLikeBehavior(driver);
    }

    public static void performSmallInteractions(WebDriver driver) {
        performHumanLikeBehavior(driver);
    }

    // Perform a small set of DOM interactions to simulate human-like behavior
    /*public static void performSmallInteractions(WebDriver driver) {
        if (driver == null) return;

        // Try Actions-based interactions first
        try {
            Actions actions = new Actions(driver);

            // move to a few offsets within the page
            actions.moveByOffset(100 + random.nextInt(200), 100 + random.nextInt(200)).pause(Duration.ofMillis(200 + random.nextInt(400)))
                    .moveByOffset(-20 - random.nextInt(80), -10 - random.nextInt(40)).pause(Duration.ofMillis(150 + random.nextInt(300))).perform();

            // small scroll using JS for reliability
            if (driver instanceof JavascriptExecutor) {
                ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, arguments[0]);", random.nextInt(200) + 50);
            }

            // focus a visible input if present and type a short random string
            List<WebElement> inputs = driver.findElements(org.openqa.selenium.By.cssSelector("input[type='text'], input[type='email'], textarea"));
            if (!inputs.isEmpty()) {
                WebElement input = inputs.get(0);
                actions.moveToElement(input).click().pause(Duration.ofMillis(150 + random.nextInt(200))).sendKeys(randomShortString()).pause(Duration.ofMillis(100)).perform();
                // clear what we typed
                input.clear();
            }

            // small mouse move to body
            actions.moveByOffset(10 + random.nextInt(60), 10 + random.nextInt(60)).perform();
            return;
        } catch (Exception ignored) {
            // fallback to JS-only if Actions are not available or fail
        }

        // JS fallback: keep the old behavior for environments where Actions fails
        if (!(driver instanceof JavascriptExecutor)) return;
        JavascriptExecutor js = (JavascriptExecutor) driver;

        try {
            // small scroll down and up
            js.executeScript("window.scrollBy(0, arguments[0]);", random.nextInt(200) + 50);
            Thread.sleep(300 + random.nextInt(400));
            js.executeScript("window.scrollBy(0, arguments[0]);", - (random.nextInt(100) + 20));

            // small mouse move simulation via dispatching mouse events to body
            String moveScript = "var e = new MouseEvent('mousemove', {clientX: arguments[0], clientY: arguments[1], bubbles:true}); document.body.dispatchEvent(e);";
            js.executeScript(moveScript, 100 + random.nextInt(200), 100 + random.nextInt(200));

            Thread.sleep(200 + random.nextInt(400));

            // focus and blur an input if exists to resemble user behavior
            String focusScript = "var input = document.querySelector('input, textarea'); if(input){ input.focus(); setTimeout(()=>{ input.blur(); }, 200);} return !!input;";
            js.executeScript(focusScript);

        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            // keep silent — non-critical
        }
    }

    private static String randomShortString() {
        String chars = "abcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder();
        int len = 3 + random.nextInt(4);
        for (int i = 0; i < len; i++) sb.append(chars.charAt(random.nextInt(chars.length())));
        return sb.toString();
    }*/
}

package me.TechsCode.TechDiscordBot.spigotmc;

import io.github.bonigarcia.wdm.WebDriverManager;
import me.TechsCode.TechDiscordBot.TechDiscordBot;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.Collections;

public class VirtualBrowser {

    protected ChromeDriver driver;

    private static final String OS = System.getProperty("os.name").toLowerCase();

    public VirtualBrowser() {
        WebDriverManager.chromedriver().setup();
        System.setProperty(ChromeDriverService.CHROME_DRIVER_SILENT_OUTPUT_PROPERTY, "true");

        ChromeOptions options = new ChromeOptions();

        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("useAutomationExtension", false);
        options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));

        if(!isWindows() && !isMac()) {
            options.addArguments("--disable-extensions");
            options.addArguments("--headless");
            options.addArguments("--disable-gpu");
            options.addArguments("--no-sandbox");
        }

        this.driver = new ChromeDriver(options);

        driver.executeScript("popup_window = window.open('https://www.spigotmc.org')");

        try {
            Thread.sleep(10000L);
        } catch (InterruptedException ignored) { }

        driver.executeScript("popup_window.close()");

        try {
            Thread.sleep(2000L);
        } catch (InterruptedException ignored) { }
    }

    public static boolean isWindows() {
        return (OS.contains("win"));
    }

    public static boolean isMac() {
        return (OS.contains("mac"));
    }

    public Document navigate(String url) {
        driver.get(url);

        // Bypass Cloudflare
        int i = 0;
        while (driver.getPageSource().contains("This process is automatic. Your browser will redirect to your requested content shortly.")) {
            if (i == 0)
                TechDiscordBot.log("Cloudflare detected. Bypassing it now...");

            sleep(1000);

            i++;
        }

        if (i != 0)
            System.out.println("Bypassed Cloudflare after " + i + " seconds");

        while (driver.getPageSource().contains("One more step") && driver.getPageSource().contains("Please complete the security check to access")) {
            sleep(1000);
            System.err.println("Detected an unsolvable captcha.. waiting...");
        }

        if (i > 10 || driver.getPageSource().contains("ERR_TOO_MANY_REDIRECTS")) {
            sleep(5000);

            TechDiscordBot.log("Taking too long... retrying to access " + url);
            navigate(url);
        }

        return Jsoup.parse(driver.getPageSource());
    }

    public void close() {
        driver.close();
    }

    public void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

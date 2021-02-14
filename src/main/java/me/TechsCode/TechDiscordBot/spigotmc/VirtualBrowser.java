package me.TechsCode.TechDiscordBot.spigotmc;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import io.github.bonigarcia.wdm.WebDriverManager;
import me.TechsCode.SpigotAPI.data.Purchase;
import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.module.modules.VerificationModule;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    public void navigate(String url) throws InterruptedException {
        driver.get(url);

        // Bypass Cloudflare
        int i = 0;
        while (driver.getPageSource().contains("This process is automatic. Your browser will redirect to your requested content shortly.")) {
            if (i == 0)
                System.out.println("Cloudflare detected. Bypassing it now...");

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

            System.out.println("Taking too long... retrying to access " + url);
            navigate(url);
        }
    }

    public void close() {
        driver.close();
    }
    public static final String BASE = "https://www.spigotmc.org";
    public static boolean st = false;

    public void collectResources(String id , String Code, String username) throws InterruptedException {
        System.out.println(Code);
        navigate(BASE + "/members/"+ id+"/");

        Document resourcesPage = Jsoup.parse(driver.getPageSource());

        for (Element item : resourcesPage.getElementsByClass("messageSimple")) {
            String name = item.getElementsByClass("username poster").text();
            String comment = item.getElementsByClass("ugc baseHtml").text();


            if (comment.equals("TechVerification."+Code)) {
                Purchase[] purchases = TechDiscordBot.getSpigotAPI().getPurchases().userId(id).toArray(new Purchase[0]);
                for (Purchase p : purchases) {
                    st = true;
                    System.out.println(p.getResource().getName());
                }

                return;
            }
            else{
                st = false;
            }
        }
    }


    public void sleep(long millis) throws InterruptedException {
        Thread.sleep(millis);
    }
}

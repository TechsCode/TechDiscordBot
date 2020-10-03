package me.TechsCode.TechDiscordBot.spigotmc;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import me.TechsCode.TechDiscordBot.TechDiscordBot;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VirtualBrowser {

    private final WebClient webClient;

    public VirtualBrowser() {
        this.webClient = new WebClient(BrowserVersion.CHROME);
        this.webClient.getOptions().setJavaScriptEnabled(true);
        this.webClient.getOptions().setTimeout(15000);
        this.webClient.getOptions().setCssEnabled(false);
        this.webClient.getOptions().setRedirectEnabled(true);
        this.webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        this.webClient.getOptions().setThrowExceptionOnScriptError(false);
        this.webClient.getOptions().setPrintContentOnFailingStatusCode(false);

        Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
    }

    public HtmlPage request(final String url, final HttpMethod httpMethod, boolean tryAgain) {
        try {
            final WebRequest wr = new WebRequest(new URL(url), httpMethod);
            final HtmlPage htmlPage = this.webClient.getPage(wr);
            if (htmlPage.asText().contains("DDoS protection by Cloudflare")) {
                TechDiscordBot.log("Cloudflare » Bypassing Cloud Flare!");

                try {
                    Thread.sleep(9000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if(!tryAgain) return null;
                return this.request(url, httpMethod);
            }

            return htmlPage;
        } catch (IOException e2) {
            e2.printStackTrace();
            return null;
        }
    }

    public HtmlPage request(final String url, final HttpMethod httpMethod) {
        return request(url, httpMethod, true);
    }
}
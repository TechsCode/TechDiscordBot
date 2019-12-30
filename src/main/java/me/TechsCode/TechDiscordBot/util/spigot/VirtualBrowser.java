package me.TechsCode.TechDiscordBot.util.spigot;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class VirtualBrowser {

    private WebClient webClient;

    public VirtualBrowser() {
        webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setTimeout(15000);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setRedirectEnabled(true);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setPrintContentOnFailingStatusCode(false);

        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
    }

    public HtmlPage request(String url, HttpMethod httpMethod) {
        try {
            WebRequest wr = new WebRequest(new URL(url), httpMethod);

            HtmlPage htmlPage = webClient.getPage(wr);

            if(htmlPage.asText().contains("DDoS protection by Cloudflare")) {
                System.out.println("Bypassing Cloud Flare..");

                try {
                    Thread.sleep(9000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Recursively trying again. Cloudflare should be bypassed next time
                return request(url, httpMethod);
            }

            return htmlPage;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}

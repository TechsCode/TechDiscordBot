package me.TechsCode.TechDiscordBot.spigotmc;

import java.util.logging.*;
import com.gargoylesoftware.htmlunit.html.*;
import java.net.*;
import com.gargoylesoftware.htmlunit.*;
import me.TechsCode.TechDiscordBot.TechDiscordBot;

import java.io.*;

public class VirtualBrowser {

    private WebClient webClient;

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

    public Page request2(final String url, final HttpMethod httpMethod) {
        try {
            final WebRequest wr = new WebRequest(new URL(url), httpMethod);
            return this.webClient.getPage(wr);
        } catch (IOException e2) {
            e2.printStackTrace();
            return null;
        }
    }

    public HtmlPage request(final String url, final HttpMethod httpMethod) {
        return request(url, httpMethod, true);
    }
}
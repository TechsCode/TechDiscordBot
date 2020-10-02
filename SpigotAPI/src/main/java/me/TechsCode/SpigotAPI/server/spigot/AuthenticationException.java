package me.TechsCode.SpigotAPI.server.spigot;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class AuthenticationException extends Exception {

    private HtmlPage page;

    public AuthenticationException(HtmlPage page) {
        this.page = page;
    }

    public HtmlPage getPage() {
        return page;
    }
}

package me.TechsCode.TechDiscordBot.util.spigot;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class SpigotMC {

    private VirtualBrowser browser;

    public SpigotMC() {
        this.browser = new VirtualBrowser();
    }

    public ProfileComment[] getComments(String userId) {
        Document doc = doc("https://spigotmc.org/members/" + userId);
        ArrayList<ProfileComment> comments = new ArrayList<>();
        int pages = 1;
        Elements pageCounter = doc.getElementsByClass("pageNavHeader");
        if(!pageCounter.isEmpty()) pages = Integer.valueOf(pageCounter.first().text().split("of ")[1]);
        for(int page = 1; page <= pages; page++) {
            doc = doc("https://spigotmc.org/members/" + userId + "?page=" + page);
            if(doc != null) {
                for(Element all : doc.getElementsByClass("messageSimple")) {
                    Element content = all.getElementsByClass("messageContent").first();
                    if(content == null) continue;
                    String href = content.getElementsByTag("a").attr("href");
                    String text = content.getElementsByTag("blockquote").text();
                    comments.add(new ProfileComment(text, getUserIdFromHref(href)));
                }
            }
        }
        return comments.toArray(new ProfileComment[comments.size()]);
    }

    private static String getUserIdFromHref(String href) {
        href = href.replace("members/", "").replace("/", "");
        String username = href.split("[.]")[0];
        String userid = href.split("[.]")[1];
        return userid;
    }

    private Document doc(String url) {
        HtmlPage htmlPage = browser.request(url, HttpMethod.GET);
        Document doc = Jsoup.parse(htmlPage.asXml());
        return doc;
    }
}

package me.TechsCode.TechDiscordBot.spigotmc;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class SpigotMC {

    private static VirtualBrowser browser;

    static {
        browser = new VirtualBrowser();
    }

    public static ProfileComment[] getComments(final String userId) {
        Document doc = doc("https://spigotmc.org/members/" + userId);
        final List<ProfileComment> comments = new ArrayList<ProfileComment>();
        int pages = 1;
        final Elements pageCounter = doc.getElementsByClass("pageNavHeader");
        if (!pageCounter.isEmpty()) {
            pages = Integer.valueOf(pageCounter.first().text().split("of ")[1]);
        }
        for (int page = 1; page <= pages; ++page) {
            doc = doc("https://spigotmc.org/members/" + userId + "?page=" + page);
            if (doc != null) {
                for (final Element all : doc.getElementsByClass("messageSimple")) {
                    final Element content = all.getElementsByClass("messageContent").first();
                    if (content == null) {
                        continue;
                    }
                    final String href = content.getElementsByTag("a").attr("href");
                    final String text = content.getElementsByTag("blockquote").text();
                    comments.add(new ProfileComment(getUserIdFromHref(href),text));
                }
            }
        }
        return comments.toArray(new ProfileComment[comments.size()]);
    }

    private static String getUserIdFromHref(String href) {
        href = href.replace("members/", "").replace("/", "");
        final String username = href.split("[.]")[0];
        final String userid = href.split("[.]")[1];
        return userid;
    }

    private static Document doc(final String url) {
        final HtmlPage htmlPage = browser.request(url, HttpMethod.GET);
        final Document doc = Jsoup.parse(htmlPage.asXml());
        return doc;
    }
}

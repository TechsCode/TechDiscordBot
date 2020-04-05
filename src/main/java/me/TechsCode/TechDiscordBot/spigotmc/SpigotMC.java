package me.TechsCode.TechDiscordBot.spigotmc;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import me.TechsCode.SpigotAPI.client.objects.User;
import me.TechsCode.TechDiscordBot.TechDiscordBot;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class SpigotMC {

    private static VirtualBrowser browser = new VirtualBrowser();

    public static ProfileComment[] getComments(final String userId) {
        Document doc = doc("https://spigotmc.org/members/" + userId);
        final List<ProfileComment> comments = new ArrayList<ProfileComment>();
        int pages = 1;
        final Elements pageCounter = doc.getElementsByClass("pageNavHeader");
        if (!pageCounter.isEmpty()) {
            pages = Integer.parseInt(pageCounter.first().text().split("of ")[1]);
        }
        for (int page = 1; page <= pages; ++page) {
            doc = doc("https://spigotmc.org/members/" + userId + "?page=" + page);
            if (doc != null) {
                for (final Element all : doc.getElementsByClass("messageSimple")) {
                    final Element content = all.getElementsByClass("messageContent").first();

                    if (content == null) continue;

                    final String href = content.getElementsByTag("a").attr("href");
                    final String commentId = all.attr("id").split("-")[2];
                    final String user = all.attr("data-author");
                    final String user2 = getUserFromHref(href).getUsername();

                    if(!user2.toLowerCase().equals(user.toLowerCase())) continue; //Make sure the user commented it.

                    final String text = content.getElementsByTag("blockquote").text();
                    comments.add(new ProfileComment(commentId, userId, text));
                }
            }
        }
        return comments.toArray(new ProfileComment[0]);
    }

    private static User getUserFromHref(String href) {
        href = href.replace("members/", "").replace("/", "");
        final String username = href.split("[.]")[0];
        final String id = href.split("[.]")[1];
        return new User(null, id, username, null);
    }

    private static Document doc(final String url) {
        final HtmlPage htmlPage = browser.request(url, HttpMethod.GET);
        return Jsoup.parse(htmlPage.asXml());
    }

    public static VirtualBrowser getBrowser() {
        return browser;
    }
}
package me.TechsCode.TechDiscordBot.spigotmc;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import me.TechsCode.SpigotAPI.data.User;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

public class SpigotMC extends VirtualBrowser{

    private final static VirtualBrowser browser = new VirtualBrowser();

    public static ProfileComment[] getComments(final String userId) throws InterruptedException {

        final List<ProfileComment> comments = new ArrayList<>();

        new VirtualBrowser().navigate("https://spigotmc.org/members/" + userId);

        Document resourcesPage = Jsoup.parse(new VirtualBrowser().driver.getPageSource());

        for (Element item : resourcesPage.getElementsByClass("messageSimple")) {
            final Element content = item.getElementsByClass("ugc baseHtml").first();

            if (content == null) continue;

            final String href = content.getElementsByTag("a").attr("href");
            final String commentId = item.attr("id").split("-")[2];
            final String user = item.attr("data-author");
            final String user2 = getUserFromHref(href).getUsername();
            final String text = content.getElementsByTag("blockquote").text();

            comments.add(new ProfileComment(commentId, userId, text));
        }
        return comments.toArray(new ProfileComment[0]);
    }

    private static User getUserFromHref(String href) {
        href = href.replace("members/", "").replace("/", "");
        final String username = href.split("[.]")[0];
        final String id = href.split("[.]")[1];

        return new User(id, username, null);
    }



    public static VirtualBrowser getBrowser() {
        return browser;
    }
}
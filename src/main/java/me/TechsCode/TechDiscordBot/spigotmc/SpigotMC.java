package me.TechsCode.TechDiscordBot.spigotmc;

import me.TechsCode.SpigotAPI.data.User;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class SpigotMC {

    private final static VirtualBrowser browser = new VirtualBrowser();

    public static ProfileComment[] getComments(final String userId) {
        Document doc = doc("https://spigotmc.org/members/" + userId);

        final List<ProfileComment> comments = new ArrayList<>();
        final Elements pageCounter = doc.getElementsByClass("pageNavHeader");
        int pages = 1;

        if (!pageCounter.isEmpty())
            pages = Integer.parseInt(pageCounter.first().text().split("of ")[1]);

        for (int page = 1; page <= pages; ++page) {
            doc = doc("https://spigotmc.org/members/" + userId + "?page=" + page);
            if (doc != null) {
                for (final Element all : doc.getElementsByClass("messageSimple")) {
                    final Element content = all.getElementsByClass("messageContent").first();

                    if (content == null)
                        continue;

                    final String href = content.getElementsByTag("a").attr("href");
                    final String commentId = all.attr("id").split("-")[2];
                    final String user = all.attr("data-author");
                    final String user2 = getUserFromHref(href).getUsername();

                    if(!user2.equalsIgnoreCase(user)) //Make sure the user commented it.
                        continue;

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

        return new User(id, username, null);
    }

    private static Document doc(final String url) {
        return Jsoup.parse(browser.navigate(url).html());
    }

    public static VirtualBrowser getBrowser() {
        return browser;
    }
}
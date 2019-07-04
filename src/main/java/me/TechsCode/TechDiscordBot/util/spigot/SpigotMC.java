package me.TechsCode.TechDiscordBot.util.spigot;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class SpigotMC {

    public static ProfileComment[] getComments(String userId){
        Document doc = doc("http://spigotmc.org/members/"+userId);

        ArrayList<ProfileComment> comments = new ArrayList<>();

        int pages = 1;
        Elements pageCounter = doc.getElementsByClass("pageNavHeader");
        if(!pageCounter.isEmpty()){
            pages = Integer.valueOf(pageCounter.first().text().split("of ")[1]);
        }

        for(int page = 1; page <= pages; page++){
            doc = doc("http://spigotmc.org/members/"+userId+"?page="+page);

            if(doc != null){
                for(Element all : doc.getElementsByClass("messageSimple")){
                    Element content = all.getElementsByClass("messageContent").first();

                    if(content == null){
                        continue;
                    }

                    String href = content.getElementsByTag("a").attr("href");
                    String text = content.getElementsByTag("blockquote").text();

                    comments.add(new ProfileComment(text, getUserIdFromHref(href)));
                }
            }
        }

        return comments.toArray(new ProfileComment[comments.size()]);
    }

    private static String getUserIdFromHref(String href){
        href = href.replace("members/", "").replace("/", "");
        String username = href.split("[.]")[0];
        String userid = href.split("[.]")[1];
        return userid;
    }

    private static Document doc(String url) {
        try {
            return Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                    .referrer("http://www.google.com")
                    .timeout(30 * 1000)
                    .maxBodySize(0).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}

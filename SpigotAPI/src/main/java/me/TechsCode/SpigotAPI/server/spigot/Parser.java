package me.TechsCode.SpigotAPI.server.spigot;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import me.TechsCode.SpigotAPI.server.data.Entry;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Parser {

    public static final String BASE = "https://www.spigotmc.org";

    private VirtualBrowser virtualBrowser;
    private String ownUserId;

    public Parser(String username, String password) throws AuthenticationException {
        virtualBrowser = new VirtualBrowser();

        HtmlPage page = virtualBrowser.request(Parser.BASE+"/login/login", HttpMethod.POST,
                new NameValuePair("cookieexists", "false"),
                new NameValuePair("login", username),
                new NameValuePair("password", password));

        this.ownUserId = getLoggedInUserId(page);

        if(ownUserId == null) throw new AuthenticationException(page);
    }

    private String getLoggedInUserId(HtmlPage htmlPage){
        Document document = Jsoup.parse(htmlPage.asXml());

        try {
            Element link = document
                    .getElementsByClass("sidebar").first()
                    .getElementsByClass("visitorPanel").first()
                    .getElementsByClass("avatar").first();

            String userId = link.attr("href")
                    .replace("/", "")
                    .split("[.]")[1];

            return userId;
        } catch (Exception e){
            return null;
        }
    }

    public List<Entry> retrieveResources(){
        List<Entry> list = new ArrayList<>();
        Document resourcesPage = getSpigotPage("resources/authors/"+ownUserId);

        for(Element item : resourcesPage.getElementsByClass("resourceListItem")){

            Elements resourceDetails = item.getElementsByClass("resourceDetails").first().getAllElements();

            String id = item.id().split("-")[1];
            String name = item.getElementsByClass("title").first().getElementsByTag("a").first().text();
            String version = item.getElementsByClass("title").first().getElementsByTag("span").first().text();
            String tagLine = item.getElementsByClass("tagLine").first().text();
            String category = resourceDetails.get(4).text();
            String costString = category.equalsIgnoreCase("premium") ? item.getElementsByClass("cost").first().text() : null;
            String icon = BASE+"/"+item.getElementsByClass("resourceIcon").select("img").attr("src");
            String time = resourceDetails.get(2).text();

            Entry entry = new Entry();
            entry.set("id", id);
            entry.set("name", name);
            entry.set("version", version);
            entry.set("tagLine", tagLine);
            entry.set("category", category);
            entry.setCost(costString);
            entry.set("icon", icon);
            entry.set("humanTime", time);
            entry.set("unixTime", 0);
            list.add(entry);
        }
        return list;
    }

    public List<Entry> retrieveUpdates(List<Entry> resources){
        List<Entry> list = new ArrayList<>();

        for(Map.Entry<Element, Entry> pair : collectElementsOfSubPage(resources, "updates", "resourceUpdate").entrySet()){
            Element element = pair.getKey();
            Entry resource = pair.getValue();

            if(element.getElementsByClass("textHeading").isEmpty()) continue; // If there are no updates, spigot redirects to the homepage. Since that div is also used there we have to block it.

            String resourceId = resource.getString("id");
            String resourceName = resource.getString("name");
            String id = element.id().split("-")[1];
            String title = element.getElementsByClass("textHeading").first().text();
            String description = element.getElementsByClass("messageText").first().text();
            String updatedAt = element.getElementsByClass("DateTime").first().text();

            Elements imageElements = element.select("img[data-url]");
            List<String> imagesEntry = imageElements.stream().map(elements -> elements.attr("data-url")).collect(Collectors.toList());

            Entry entry = new Entry();
            entry.set("id", id);
            entry.set("resourceId", resourceId);
            entry.set("resourceName", resourceName);
            entry.set("title", title);
            entry.set("description", description);
            entry.set("humanTime", updatedAt);
            entry.set("unixTime", 0);
            entry.set("images", imagesEntry);
            list.add(entry);
        }
        return list;
    }

    public List<Entry> retrieveReviews(List<Entry> resources){
        List<Entry> list = new ArrayList<>();

        for(Map.Entry<Element, Entry> pair : collectElementsOfSubPage(resources, "reviews", "review").entrySet()) {
            Element element = pair.getKey();
            Entry resource = pair.getValue();

            String id = element.id().split("-")[1];
            String resourceId = resource.getString("id");
            String resourceName = resource.getString("name");
            String text = element.getElementsByTag("blockquote").text().replace("<br>", "\n");
            int rating = Math.round(Float.parseFloat(element.getElementsByClass("ratings").first().attr("title")));
            String username = element.attr("data-author");
            String userId = element.id().split("-")[2];
            String time = element.getElementsByClass("DateTime").first().text();
            String avatarUrl = element.select("img").attr("src");

            Entry entry = new Entry();
            entry.set("id", id);
            entry.set("resourceId", resourceId);
            entry.set("resourceName", resourceName);
            entry.set("text", text);
            entry.set("rating", rating);
            entry.set("username", username);
            entry.set("userId", userId);
            entry.set("avatarUrl", parseAvatarUrl(avatarUrl));
            entry.set("humanTime", time);
            entry.set("unixTime", 0);
            list.add(entry);
        }
        return list;
    }

    public List<Entry> retrievePurchases(List<Entry> resources){
        List<Entry> list = new ArrayList<>();

        for(Map.Entry<Element, Entry> pair : collectElementsOfSubPage(resources, "buyers", "memberListItem").entrySet()) {
            Element element = pair.getKey();
            Entry resource = pair.getValue();

            Element link = element.getElementsByClass("StatusTooltip").first();

            Element costElement = element
                    .getElementsByClass("extra").last()
                    .getAllElements().last();

            String username = link.text();
            String userId = link.attr("href").replace("/", "").split("[.]")[1];
            String time = element.getElementsByClass("DateTime").first().text();
            String costString = costElement.tagName().equalsIgnoreCase("div") ? costElement.text().split(": ")[1] : null;
            String avatarUrl = element.getElementsByClass("s").first().attr("style").split("'")[1].split("'")[0];

            Entry entry = new Entry();
            String resourceId = resource.getString("id");
            String resourceName = resource.getString("name");
            entry.set("id", resourceId + "-" + userId);
            entry.set("resourceId", resourceId);
            entry.set("resourceName", resourceName);
            entry.set("username", username);
            entry.set("userId", userId);
            entry.set("avatarUrl", parseAvatarUrl(avatarUrl));
            entry.set("humanTime", time);
            entry.set("unixTime", 0);
            entry.setCost(costString);
            list.add(entry);
        }
        return list;
    }

    private Map<Element, Entry> collectElementsOfSubPage(List<Entry> resources, String subPage, String uniqueClassName){
        HashMap<Element, Entry> map = new HashMap<>();

        for(Entry resource : resources){
            String resourceId = resource.getString("id");
            String category = resource.getString("category");

            if(!category.equalsIgnoreCase("premium")) continue;

            int pageAmount = 1;
            int currentPage = 1;

            while (currentPage <= pageAmount){
                Document document = getSpigotPage("resources/"+resourceId+"/"+subPage+"?page="+currentPage);

                for(Element element : document.getElementsByClass(uniqueClassName)){
                    map.put(element, resource);
                }

                pageAmount = getPageAmount(document);
                currentPage++;
            }
        }
        return map;
    }

    private int getPageAmount(Document document){
        if(document.getElementsByClass("pageNavHeader").isEmpty()) return 1;

        Element pageNavHeader = document.getElementsByClass("pageNavHeader").first();
        String value = pageNavHeader.text().split(" of ")[1];
        return Integer.parseInt(value);
    }

    private Document getSpigotPage(String subPage){
        HtmlPage htmlPage = virtualBrowser.request(BASE+"/"+subPage, HttpMethod.GET);
        return Jsoup.parse(htmlPage.asXml());
    }

    private String parseAvatarUrl(String url) {
        if(url.startsWith("/data")) return BASE + url;
        if(url.startsWith("//static")) return "https:" + url;
        return url;
    }

    public String getOwnUserId() {
        return ownUserId;
    }
}

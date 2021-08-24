package me.TechsCode.TechDiscordBot.spigotmc;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.spigotmc.data.*;
import me.TechsCode.TechDiscordBot.spigotmc.data.lists.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SpigotAPIManager {
    private static String base_url;
    private static String apiToken;

    public SpigotAPIManager(String url, String token) {
        base_url = url;
        apiToken = token;
    }

    private JsonObject makeRequest(String endPoint, String attributes) {
        try {
            URL url = new URL(base_url + endPoint + "?token=" + apiToken + attributes);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();

            Gson gson = new Gson();
            return gson.fromJson(content.toString(), JsonObject.class);
        } catch (Exception e) {
            JsonObject errorObj = new JsonObject();
            errorObj.addProperty("status", "error");
            errorObj.addProperty("msg", e.getMessage());
            return errorObj;
        }
    }

    public APIWebStatus getStatus() {
        JsonObject obj = makeRequest("status", "");

        if (obj.has("status")) {
            if (obj.get("status").getAsString().equals("error")) {
                return new APIWebStatus(false, false, 0, 0, "Unknown", "Unknown");
            }
        }

        return new APIWebStatus(obj.get("spigotFetching").getAsBoolean(), obj.get("marketFetching").getAsBoolean(), obj.get("lastSpigotFetch").getAsLong(), obj.get("lastMarketFetch").getAsLong(), obj.get("lastSpigotFetchDate").getAsString(), obj.get("lastMarketFetchDate").getAsString());
    }

    //SPIGOT
    public ProfileCommentList fetchSpigotProfileComments(String userid, boolean showAll) {
        JsonObject obj = makeRequest("spigot/verifyUser", "&user=" + userid + "&showAll=" + showAll);
        ProfileCommentList comments = new ProfileCommentList();

        if (obj.has("status")) {
            if (obj.get("status").getAsString().equals("error")) {
                return comments;
            }
        }
        if (!obj.has("data")) {
            return comments;
        }

        JsonArray arr = obj.get("data").getAsJsonArray();

        for (JsonElement jsonElement : arr) {
            JsonObject comment = jsonElement.getAsJsonObject();

            comments.add(new ProfileComment(
                    comment.get("commentId").getAsString(),
                    comment.get("userId").getAsString(),
                    comment.get("message").getAsString()));
        }

        return comments;
    }

    public ResourcesList fetchSpigotResource() {
        JsonObject obj = makeRequest("data/spigot/resources", "");
        ResourcesList resources = new ResourcesList();

        if (obj.has("status")) {
            if (obj.get("status").getAsString().equals("error")) {
                return resources;
            }
        }
        if (!obj.has("data")) {
            return resources;
        }

        JsonArray arr = obj.get("data").getAsJsonArray();

        for (JsonElement jsonElement : arr) {
            JsonObject resource = jsonElement.getAsJsonObject();
            JsonObject time = resource.get("time").getAsJsonObject();

            Cost pluginCost = null;
            if (resource.has("cost")) {
                JsonObject cost = resource.get("cost").getAsJsonObject();
                pluginCost = new Cost(cost.get("currency").getAsString(), cost.get("value").getAsFloat());
            }

            resources.add(new Resource(
                    resource.get("id").getAsString(),
                    resource.get("name").getAsString(),
                    resource.get("tagLine").getAsString(),
                    resource.get("category").getAsString(),
                    resource.get("version").getAsString(),
                    pluginCost,
                    new Time(time.get("human").getAsString(), time.get("unix").getAsInt())
                    , "spigot"));
        }

        return resources;
    }

    public ReviewsList fetchSpigotReviews() {
        JsonObject obj = makeRequest("data/spigot/reviews", "");
        ReviewsList reviews = new ReviewsList();

        if (obj.has("status")) {
            if (obj.get("status").getAsString().equals("error")) {
                return reviews;
            }
        }
        if (!obj.has("data")) {
            return reviews;
        }

        JsonArray arr = obj.get("data").getAsJsonArray();

        for (JsonElement jsonElement : arr) {
            JsonObject review = jsonElement.getAsJsonObject();
            JsonObject time = review.get("time").getAsJsonObject();

            reviews.add(new Review(
                    review.get("id").getAsString(),
                    review.get("resourceId").getAsString(),
                    new User(review.get("user").getAsJsonObject()),
                    review.get("text").getAsString(),
                    review.get("rating").getAsInt(),
                    new Time(time.get("human").getAsString(), time.get("unix").getAsInt())));
        }

        return reviews;
    }

    public UpdatesList fetchSpigotUpdates() {
        JsonObject obj = makeRequest("data/spigot/updates", "");
        UpdatesList updates = new UpdatesList();

        if (obj.has("status")) {
            if (obj.get("status").getAsString().equals("error")) {
                return updates;
            }
        }
        if (!obj.has("data")) {
            return updates;
        }

        JsonArray arr = obj.get("data").getAsJsonArray();

        for (JsonElement jsonElement : arr) {
            JsonObject review = jsonElement.getAsJsonObject();
            JsonObject time = review.get("time").getAsJsonObject();

            updates.add(new Update(
                    review.get("id").getAsString(),
                    review.get("resourceId").getAsString(),
                    review.get("title").getAsString(),
                    review.get("images").getAsString().split(";"),
                    review.get("description").getAsString(),
                    new Time(time.get("human").getAsString(), time.get("unix").getAsInt())));
        }

        return updates;
    }

    public PurchasesList fetchSpigotPurchases() {
        JsonObject obj = makeRequest("data/spigot/purchases", "");
        PurchasesList purchases = new PurchasesList();

        if (obj.has("status")) {
            if (obj.get("status").getAsString().equals("error")) {
                return purchases;
            }
        }
        if (!obj.has("data")) {
            return purchases;
        }

        JsonArray arr = obj.get("data").getAsJsonArray();

        for (JsonElement jsonElement : arr) {
            JsonObject purchase = jsonElement.getAsJsonObject();
            JsonObject time = purchase.get("time").getAsJsonObject();

            Cost pluginCost = null;
            if (purchase.has("cost")) {
                JsonObject cost = purchase.get("cost").getAsJsonObject();
                pluginCost = new Cost(cost.get("currency").getAsString(), cost.get("value").getAsFloat());
            }

            purchases.add(new Purchase(
                    purchase.get("resourceId").getAsString(),
                    new User(purchase.get("user").getAsJsonObject()),
                    new Time(time.get("human").getAsString(), time.get("unix").getAsInt()),
                    pluginCost));
        }

        return purchases;
    }

    //Market
    public ProfileCommentList fetchMarketProfileComments(String userid, boolean showAll) {
        JsonObject obj = makeRequest("market/verifyUser", "&user=" + userid + "&showAll=" + showAll);
        ProfileCommentList comments = new ProfileCommentList();

        if (obj.has("status")) {
            if (obj.get("status").getAsString().equals("error")) {
                return comments;
            }
        }
        if (!obj.has("data")) {
            return comments;
        }

        JsonArray arr = obj.get("data").getAsJsonArray();

        for (JsonElement jsonElement : arr) {
            JsonObject comment = jsonElement.getAsJsonObject();

            comments.add(new ProfileComment(
                    comment.get("commentId").getAsString(),
                    comment.get("userId").getAsString(),
                    comment.get("message").getAsString()));
        }

        return comments;
    }

    public ResourcesList fetchMarketResource() {
        JsonObject obj = makeRequest("data/market/resources", "");
        ResourcesList resources = new ResourcesList();

        if (obj.has("status")) {
            if (obj.get("status").getAsString().equals("error")) {
                return resources;
            }
        }
        if (!obj.has("data")) {
            return resources;
        }

        JsonArray arr = obj.get("data").getAsJsonArray();

        for (JsonElement jsonElement : arr) {
            JsonObject resource = jsonElement.getAsJsonObject();
            JsonObject time = resource.get("time").getAsJsonObject();

            Cost pluginCost = null;
            if (resource.has("cost")) {
                JsonObject cost = resource.get("cost").getAsJsonObject();
                pluginCost = new Cost(cost.get("currency").getAsString(), cost.get("value").getAsFloat());
            }

            resources.add(new Resource(
                    resource.get("id").getAsString(),
                    resource.get("name").getAsString(),
                    resource.get("tagLine").getAsString(),
                    resource.get("category").getAsString(),
                    resource.get("version").getAsString(),
                    pluginCost,
                    new Time(time.get("human").getAsString(), time.get("unix").getAsInt())
                    , "market"));
        }

        return resources;
    }

    public ReviewsList fetchMarketReviews() {
        JsonObject obj = makeRequest("data/market/reviews", "");
        ReviewsList reviews = new ReviewsList();

        if (obj.has("status")) {
            if (obj.get("status").getAsString().equals("error")) {
                return reviews;
            }
        }
        if (!obj.has("data")) {
            return reviews;
        }

        JsonArray arr = obj.get("data").getAsJsonArray();

        for (JsonElement jsonElement : arr) {
            JsonObject review = jsonElement.getAsJsonObject();
            JsonObject time = review.get("time").getAsJsonObject();

            reviews.add(new Review(
                    review.get("id").getAsString(),
                    review.get("resourceId").getAsString(),
                    new User(review.get("user").getAsJsonObject()),
                    review.get("text").getAsString(),
                    review.get("rating").getAsInt(),
                    new Time(time.get("human").getAsString(), time.get("unix").getAsInt())));
        }

        return reviews;
    }

    public UpdatesList fetchMarketUpdates() {
        JsonObject obj = makeRequest("data/market/updates", "");
        UpdatesList updates = new UpdatesList();

        if (obj.has("status")) {
            if (obj.get("status").getAsString().equals("error")) {
                return updates;
            }
        }
        if (!obj.has("data")) {
            return updates;
        }

        JsonArray arr = obj.get("data").getAsJsonArray();

        for (JsonElement jsonElement : arr) {
            JsonObject review = jsonElement.getAsJsonObject();
            JsonObject time = review.get("time").getAsJsonObject();

            updates.add(new Update(
                    review.get("id").getAsString(),
                    review.get("resourceId").getAsString(),
                    review.get("title").getAsString(),
                    review.get("images").getAsString().split(";"),
                    review.get("description").getAsString(),
                    new Time(time.get("human").getAsString(), time.get("unix").getAsInt())));
        }

        return updates;
    }

    public PurchasesList fetchMarketPurchases() {
        JsonObject obj = makeRequest("data/market/purchases", "");
        PurchasesList purchases = new PurchasesList();

        if (obj.has("status")) {
            if (obj.get("status").getAsString().equals("error")) {
                return purchases;
            }
        }
        if (!obj.has("data")) {
            return purchases;
        }

        JsonArray arr = obj.get("data").getAsJsonArray();

        for (JsonElement jsonElement : arr) {
            JsonObject review = jsonElement.getAsJsonObject();
            JsonObject time = review.get("time").getAsJsonObject();

            Cost pluginCost = null;
            if (review.has("cost")) {
                JsonObject cost = review.get("cost").getAsJsonObject();
                pluginCost = new Cost(cost.get("currency").getAsString(), cost.get("value").getAsFloat());
            }

            purchases.add(new Purchase(
                    review.get("").getAsString(),
                    new User(review.get("user").getAsJsonObject()),
                    new Time(time.get("human").getAsString(), time.get("unix").getAsInt()),
                    pluginCost));
        }

        return purchases;
    }

    public boolean stopAPI(){
        JsonObject obj = makeRequest("actions/stop", "");
        if (obj.has("Status")) {
            return !obj.get("Status").getAsString().equals("Error");
        }else{
            return false;
        }
    }

    public boolean restartAPI() {
        JsonObject obj = makeRequest("actions/restart", "");
        if (obj.has("Status")) {
            return !obj.get("Status").getAsString().equals("Error");
        }else{
            return false;
        }
    }
}

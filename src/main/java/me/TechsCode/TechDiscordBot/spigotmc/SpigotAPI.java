package me.TechsCode.TechDiscordBot.spigotmc;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.TechsCode.TechDiscordBot.spigotmc.data.*;
import me.TechsCode.TechDiscordBot.spigotmc.data.ProfileComment;
import me.TechsCode.TechDiscordBot.spigotmc.data.lists.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SpigotAPI {
    private static String base_url;
    private static String apiToken;

    public SpigotAPI(String url, String token){
        base_url = url;
        apiToken = token;
    }

    private JsonObject makeRequest(String endPoint, String attributes){
        Gson gson = new Gson();
        JsonObject object = new JsonObject();

        try{
            URL url = new URL(base_url + endPoint + "?token="+ apiToken + attributes);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            int status = con.getResponseCode();
            if(status == 200){
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                object = gson.fromJson(content.toString(), JsonObject.class);
            }else{
                JsonObject errorObj = new JsonObject();
                errorObj.addProperty("status", "error");
                errorObj.addProperty("msg", "Error getting data");
                object = errorObj;
            }
            con.disconnect();
        }catch (Exception e){
            JsonObject errorObj = new JsonObject();
            errorObj.addProperty("status", "error");
            errorObj.addProperty("msg", e.getMessage());
            object = errorObj;
        }

        return object;
    }

    public APIWebStatus getStatus(){
        JsonObject obj = makeRequest("status", "");

        if(obj.has("status")){
            if(obj.get("status").getAsString().equals("error")){
                return new APIWebStatus("offline", 404, "offline", 404, 0,"Unknown");
            }
        }

        return new APIWebStatus(obj.get("spigotStatus").getAsString(), obj.get("spigotCode").getAsInt(), obj.get("marketStatus").getAsString(), obj.get("marketCode").getAsInt(), obj.get("lastFetch").getAsInt(), obj.get("lastFetchDate").getAsString());
    }

    //SPIGOT
    public ProfileCommentList getSpigotProfileComments(String userid, boolean showAll){
        JsonObject obj = makeRequest("data/spigot/verify", "?user="+userid+"?showall="+showAll);
        ProfileCommentList comments = new ProfileCommentList();

        if(obj.has("status")){
            if(obj.get("status").getAsString().equals("error")){
                return comments;
            }
        }
        if(!obj.has("data")){
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

    public ResourcesList getSpigotResource(){
        JsonObject obj = makeRequest("data/spigot/resources", "");
        ResourcesList resources = new ResourcesList();

        if(obj.has("status")){
            if(obj.get("status").getAsString().equals("error")){
                return resources;
            }
        }
        if(!obj.has("data")){
            return resources;
        }

        JsonArray arr = obj.get("data").getAsJsonArray();

        for (JsonElement jsonElement : arr) {
            JsonObject resource = jsonElement.getAsJsonObject();
            JsonObject time = resource.get("time").getAsJsonObject();

            Cost pluginCost = null;
            if(resource.has("cost")){
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

    public ReviewsList getSpigotReviews(){
        JsonObject obj = makeRequest("data/spigot/reviews", "");
        ReviewsList reviews = new ReviewsList();

        if(obj.has("status")){
            if(obj.get("status").getAsString().equals("error")){
                return reviews;
            }
        }
        if(!obj.has("data")){
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

    public UpdatesList getSpigotUpdates(){
        JsonObject obj = makeRequest("data/spigot/updates", "");
        UpdatesList updates = new UpdatesList();

        if(obj.has("status")){
            if(obj.get("status").getAsString().equals("error")){
                return updates;
            }
        }
        if(!obj.has("data")){
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

    public PurchasesList getSpigotPurchases(){
        JsonObject obj = makeRequest("data/spigot/purchases", "");
        PurchasesList purchases = new PurchasesList();

        if(obj.has("status")){
            if(obj.get("status").getAsString().equals("error")){
                return purchases;
            }
        }
        if(!obj.has("data")){
            return purchases;
        }

        JsonArray arr = obj.get("data").getAsJsonArray();

        for (JsonElement jsonElement : arr) {
            JsonObject purchase = jsonElement.getAsJsonObject();
            JsonObject time = purchase.get("time").getAsJsonObject();

            Cost pluginCost = null;
            if(purchase.has("cost")){
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
    public ProfileCommentList getMarketProfileComments(String userid, boolean showAll){
        JsonObject obj = makeRequest("data/market/verify", "?user="+userid+"?showall="+showAll);
        ProfileCommentList comments = new ProfileCommentList();

        if(obj.has("status")){
            if(obj.get("status").getAsString().equals("error")){
                return comments;
            }
        }
        if(!obj.has("data")){
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

    public ResourcesList getMarketResource(){
        JsonObject obj = makeRequest("data/market/resources", "");
        ResourcesList resources = new ResourcesList();

        if(obj.has("status")){
            if(obj.get("status").getAsString().equals("error")){
                return resources;
            }
        }
        if(!obj.has("data")){
            return resources;
        }

        JsonArray arr = obj.get("data").getAsJsonArray();

        for (JsonElement jsonElement : arr) {
            JsonObject resource = jsonElement.getAsJsonObject();
            JsonObject time = resource.get("time").getAsJsonObject();

            Cost pluginCost = null;
            if(resource.has("cost")){
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
                    ,"market"));
        }

        return resources;
    }

    public ReviewsList getMarketReviews(){
        JsonObject obj = makeRequest("data/market/reviews", "");
        ReviewsList reviews = new ReviewsList();

        if(obj.has("status")){
            if(obj.get("status").getAsString().equals("error")){
                return reviews;
            }
        }
        if(!obj.has("data")){
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

    public UpdatesList getMarketUpdates(){
        JsonObject obj = makeRequest("data/market/updates", "");
        UpdatesList updates = new UpdatesList();

        if(obj.has("status")){
            if(obj.get("status").getAsString().equals("error")){
                return updates;
            }
        }
        if(!obj.has("data")){
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

    public PurchasesList getMarketPurchases(){
        JsonObject obj = makeRequest("data/market/purchases", "");
        PurchasesList purchases = new PurchasesList();

        if(obj.has("status")){
            if(obj.get("status").getAsString().equals("error")){
                return purchases;
            }
        }
        if(!obj.has("data")){
            return purchases;
        }

        JsonArray arr = obj.get("data").getAsJsonArray();

        for (JsonElement jsonElement : arr) {
            JsonObject review = jsonElement.getAsJsonObject();
            JsonObject time = review.get("time").getAsJsonObject();

            Cost pluginCost = null;
            if(review.has("cost")){
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
}

package me.TechsCode.SpigotAPI.client.objects;

import me.TechsCode.SpigotAPI.client.SpigotAPIClient;
import me.TechsCode.SpigotAPI.client.collections.PurchaseCollection;
import me.TechsCode.SpigotAPI.client.collections.ReviewCollection;
import me.TechsCode.SpigotAPI.client.collections.UpdateCollection;
import org.json.simple.JSONObject;

public class Resource extends APIObject {

    public Resource(SpigotAPIClient client, JSONObject jsonObject) {
        super(client, jsonObject);
    }

    public String getId() {
        return getStringProperty("id");
    }

    public String getName() {
        return getStringProperty("name");
    }

    public String getTagLine() {
        return getStringProperty("tagLine");
    }

    public String getCategory() {
        return getStringProperty("category");
    }

    public String getIcon() {
        return getStringProperty("icon");
    }

    public String getVersion() {
        return getStringProperty("version");
    }

    public Cost getCost() {
        return new Cost(getDoubleProperty("costValue"), "EUR");
    }

    public Time getTime() {
        return new Time(getStringProperty("humanTime"), getLongProperty("unixTime"));
    }

    public UpdateCollection getUpdates(){
        return client.getUpdates().resourceId(getId());
    }

    public ReviewCollection getReviews(){
        return client.getReviews().resourceId(getId());
    }

    public PurchaseCollection getPurchases(){
        return client.getPurchases().resourceId(getId());
    }

    public boolean isPremium(){
        return getCategory().equalsIgnoreCase("premium");
    }
}

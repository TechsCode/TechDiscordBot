package me.TechsCode.TechDiscordBot.spigotmc.data;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;

public class Cost {

    private final String currency;
    private final float value;

    public Cost(JsonObject jsonObject){
        this.currency = jsonObject.get("currency").getAsString();
        this.value = jsonObject.get("value").getAsFloat();
    }

    public Cost(String combined){
        Preconditions.checkArgument(combined.contains(" "), "Invalid Combined String, is not seperated");

        value = Float.parseFloat(combined.split(" ")[0]);
        currency = combined.split(" ")[1];
    }

    public Cost(String currency, float value) {
        this.currency = currency;
        this.value = value;
    }

    public String getCurrency() {
        return currency;
    }

    public float getValue() {
        return value;
    }

    public JsonObject toJsonObject(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("currency", currency);
        jsonObject.addProperty("value", value);

        return jsonObject;
    }
}

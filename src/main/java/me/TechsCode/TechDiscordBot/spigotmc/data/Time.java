package me.TechsCode.TechDiscordBot.spigotmc.data;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;

import java.time.Month;
import java.util.Arrays;
import java.util.Calendar;

public class Time {

    private final String humanTime;
    private final long unixTime;

    public Time(JsonObject jsonObject){
        this.humanTime = jsonObject.get("human").getAsString();
        this.unixTime = jsonObject.get("unix").getAsLong();
    }

    public Time(String humanTime, long unixTime) {
        this.humanTime = humanTime;
        this.unixTime = unixTime;
    }

    public Time(String humanTime){
        Preconditions.checkArgument(humanTime.length() > 5, "Invalid Time Value (Too short)");
        this.humanTime = humanTime;

        String date = humanTime.split(" at ")[0].replace(",", "");
        String time = humanTime.split(" at ")[1];

        String monthString = date.split(" ")[0].toLowerCase();
        int month = Arrays.stream(Month.values()).filter(x -> x.name().toLowerCase().startsWith(monthString)).findFirst().get().getValue();
        int day = Integer.parseInt(date.split(" ")[1]);
        int year = Integer.parseInt(date.split(" ")[2]);

        boolean pm = date.endsWith("PM");
        String substring = time.substring(0, time.length() - 3);

        int hour = Integer.parseInt(substring.split(":")[0]);
        int minute = Integer.parseInt(substring.split(":")[1]);

        Calendar c = Calendar.getInstance();
        c.set(year, month - 1, day, pm ? hour + 12 : hour, minute);

        this.unixTime = c.getTimeInMillis();
    }

    public String getHumanTime() {
        return humanTime;
    }

    public long getUnixTime() {
        return unixTime;
    }

    public JsonObject toJsonObject(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("human", humanTime);
        jsonObject.addProperty("unix", unixTime);
        return jsonObject;
    }
}

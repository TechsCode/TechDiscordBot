package me.TechsCode.SpigotAPI.client.objects;

public class Time {

    private String humanTime;
    private long unixTime;

    public Time(String humanTime, long unixTime) {
        this.humanTime = humanTime;
        this.unixTime = unixTime;
    }

    public String getHumanTime() {
        return humanTime;
    }

    public long getUnixTime() {
        return unixTime;
    }
}
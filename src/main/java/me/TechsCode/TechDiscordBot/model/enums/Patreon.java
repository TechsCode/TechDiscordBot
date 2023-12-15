package me.techscode.techdiscordbot.model.enums;

public enum Patreon {

    TRAVELER(1, "Traveler"),
    ADVANTURER(2, "Advanturer"),
    PIONEER(3, "Pioneer"),
    WIZARD(4, "Wizard");

    private int id;
    private String name;

    Patreon(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

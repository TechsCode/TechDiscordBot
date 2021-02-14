package me.TechsCode.TechDiscordBot.objects;

public enum TicketPriority {

    LOW(0),
    MEDIUM(1),
    HIGH(2);

    private final int i;

    TicketPriority(int i) {
        this.i = i;
    }

    public int getValue() {
        return i;
    }
}
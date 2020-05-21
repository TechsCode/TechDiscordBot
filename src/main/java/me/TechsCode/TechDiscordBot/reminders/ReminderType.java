package me.TechsCode.TechDiscordBot.reminders;

public enum ReminderType {

    CHANNEL(0),
    DMs(1);

    private int i;

    ReminderType(int i) {
        this.i = i;
    }

    public int getI() {
        return i;
    }
}

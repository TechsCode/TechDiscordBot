package me.TechsCode.TechDiscordBot.reminders;

public class ReminderArgResponse {

    private long time;
    private int amountOfArgs;
    private String timeHuman;

    public ReminderArgResponse(long time, int amountOfArgs, String timeHuman) {
        this.time = time;
        this.amountOfArgs = amountOfArgs;
        this.timeHuman = timeHuman;
    }

    public long getTime() {
        return time;
    }

    public int getAmountOfArgs() {
        return amountOfArgs;
    }

    public String getTimeHuman() {
        return timeHuman;
    }
}

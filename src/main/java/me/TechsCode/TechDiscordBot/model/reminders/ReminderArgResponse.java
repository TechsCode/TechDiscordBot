package me.techscode.techdiscordbot.model.reminders;

public class ReminderArgResponse {

    private final long time;
    private final int amountOfArgs;
    private final String timeHuman;

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

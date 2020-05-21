package me.TechsCode.TechDiscordBot.reminders;

import java.util.concurrent.TimeUnit;

public enum ReminderTimeType {

    YEAR(TimeUnit.DAYS, 365, "year", "years", "yr", "yrs"),
    MONTH(TimeUnit.DAYS, 31, "months", "month"),
    WEEK(TimeUnit.DAYS, 7, "week", "weeks"),
    DAY(TimeUnit.DAYS, 1, "day", "days"),
    HOUR(TimeUnit.HOURS, 1, "hr", "hour", "hours", "hour"),
    MINUTE(TimeUnit.MINUTES, 1, "min", "mins", "minutes", "minute"),
    SECOND(TimeUnit.SECONDS, 1, "sec", "secs", "seconds", "second");

    private String[] names;
    private int multiplier;
    private TimeUnit timeUnit;

    ReminderTimeType(TimeUnit unit, int multiplier, String... names) {
        this.names = names;
        this.multiplier = multiplier;
        this.timeUnit = unit;
    }

    public long toMilli(int am) {
        return timeUnit.toMillis(am) * multiplier;
    }

    public int getMultiplier() {
        return multiplier;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public String[] getNames() {
        return names;
    }
}

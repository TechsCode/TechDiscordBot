package me.techscode.techdiscordbot.model.reminders;

import java.util.concurrent.TimeUnit;

public enum ReminderTimeType {

	YEAR(TimeUnit.DAYS, 365, "year", "years"),
	MONTH(TimeUnit.DAYS, 31, "months", "month"),
	WEEK(TimeUnit.DAYS, 7, "week", "weeks", "w"),
	DAY(TimeUnit.DAYS, 1, "day", "days", "d"),
	HOUR(TimeUnit.HOURS, 1, "hr", "hour", "hours", "hour", "h"),
	MINUTE(TimeUnit.MINUTES, 1, "min", "mins", "minutes", "minute", "m"),
	SECOND(TimeUnit.SECONDS, 1, "sec", "secs", "seconds", "second", "s");

	private final String[] names;
	private final int multiplier;
	private final TimeUnit timeUnit;

	ReminderTimeType(final TimeUnit unit, final int multiplier, final String... names) {
		this.names = names;
		this.multiplier = multiplier;
		this.timeUnit = unit;
	}

	public long toMilli(final int am) {
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

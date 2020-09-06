package me.TechsCode.TechDiscordBot.reminders;

import me.TechsCode.TechDiscordBot.util.ProjectUtil;

public class HumanTimeBuilder {

    private int seconds = 0;
    private int minutes = 0;
    private int hours = 0;
    private int days = 0;
    private int weeks = 0;
    private int months = 0;
    private int years = 0;

    public void addX(ReminderTimeType type, int amount) {
        switch(type) {
            case SECOND:
                this.seconds = this.seconds + amount;
                break;
            case MINUTE:
                this.minutes = this.minutes + amount;
                break;
            case HOUR:
                this.hours = this.hours + amount;
                break;
            case DAY:
                this.days = this.days + amount;
                break;
            case WEEK:
                this.weeks = this.weeks + amount;
                break;
            case MONTH:
                this.months = this.months + amount;
                break;
            case YEAR:
                this.years = this.years + amount;
                break;
            default:
                break;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if(years > 0) {
            sb.append(years).append(" year").append(plural(years, true));
        }
        if(months > 0) {
            sb.append(months).append(" month").append(plural(months, true));
        }
        if(weeks > 0) {
            sb.append(weeks).append(" week").append(plural(weeks, true));
        }
        if(days > 0) {
            sb.append(days).append(" day").append(plural(days, true));
        }
        if(hours > 0) {
            sb.append(hours).append(" hour").append(plural(hours, true));
        }
        if(minutes > 0) {
            sb.append(minutes).append(" minute").append(plural(minutes, true));
        }
        if(seconds > 0) {
            sb.append(seconds).append(" second").append(plural(seconds, false));
        }

        String string = sb.toString();
        if(string.endsWith(",")) string = ProjectUtil.removeEnd(string, 1) + ".";
        return string;
    }

    public String plural(int am, boolean space) {
        if(am == 1) return (space ? ", " : ".");
        return "s" + (space ? ", " : ".");
    }
}

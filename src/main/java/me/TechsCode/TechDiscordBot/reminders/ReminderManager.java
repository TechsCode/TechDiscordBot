package me.TechsCode.TechDiscordBot.reminders;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ReminderManager {

    private final List<Reminder> reminders = new ArrayList<>();

    public void load() {
        this.reminders.addAll(TechDiscordBot.getStorage().retrieveReminders());

        checkForReminders();
    }

    private void checkForReminders() {
        new Thread(() -> {
            while (true) {
                for (Reminder reminder : new ArrayList<>(reminders)) {
                    if (Math.abs(System.currentTimeMillis() - reminder.getTime()) < 100L) {
                        reminder.send();
                        reminders.remove(reminder);
                    }
                }
            }
        }).start();
    }

    public List<Reminder> getReminders() {
        return this.reminders;
    }

    public List<Reminder> getRemindersByUser(User user) {
        return this.reminders.stream().filter(reminder -> reminder.getUserId().equals(user.getId())).collect(Collectors.toList());
    }

    public Reminder createReminder(User user, String time, String remind, TextChannel channel) {
        ReminderArgResponse argResponse = argsToTime(time.split(" "));
        if(argResponse == null) return null;

        if(argResponse.getAmountOfArgs() == 0) {
            return null;
        } else {
            List<String> reminder = new ArrayList<>(Arrays.asList(remind.split(" ")));
            boolean isDM = false;

            if(reminder.get(reminder.size() - 1).equalsIgnoreCase("dms") || reminder.get(reminder.size() - 1).equalsIgnoreCase("dm")) {
                reminder = reminder.subList(0, reminder.size() - 1);
                isDM = true;
            }

//            reminder = reminder.subList(argResponse.getAmountOfArgs(), reminder.size());
            if(reminder.size() == 0) return null;

            Reminder r = new Reminder(user.getId(), channel.getId(), argResponse.getTime(), argResponse.getTimeHuman(), (isDM ? ReminderType.DMs : ReminderType.CHANNEL), String.join(" ", reminder));
            this.reminders.add(r);

            TechDiscordBot.getStorage().saveReminder(r);
            return r;
        }
    }

    public ReminderArgResponse argsToTime(String[] args) {
        HumanTimeBuilder bhb = new HumanTimeBuilder();
        long time = System.currentTimeMillis();
        int argsAm = 0;
        int i = 0;

        for(String arg : args) {
            for(ReminderTimeType rtt : ReminderTimeType.values()) {
                if(Arrays.stream(rtt.getNames()).anyMatch(n -> n.equalsIgnoreCase(arg))) {
                    if (i > 0) {
                        String tim = args[i - 1];
                        try {
                            int timint = Math.abs(Integer.parseInt(tim));
                            time = time + rtt.toMilli(timint);
                            bhb.addX(rtt, timint);
                            argsAm = argsAm + 2;
                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                }
            }
            i++;
        }

        return new ReminderArgResponse(time, argsAm, bhb.toString());
    }

    public void deleteReminder(Reminder reminder) {
        reminder.delete();
        this.reminders.remove(reminder);
    }
}

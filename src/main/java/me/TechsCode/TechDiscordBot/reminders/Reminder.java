package me.TechsCode.TechDiscordBot.reminders;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class Reminder {

    private final String userId, channelId, humanTime, reminder;
    private final long time;
    private final ReminderType type;

    public Reminder(String userId, String channelId, long time, String humanTime, ReminderType type, String reminder) {
        this.userId = userId;
        this.channelId = channelId;
        this.time = time;
        this.humanTime = humanTime;
        this.type = (channelId == null ? ReminderType.DMs : type);
        this.reminder = reminder;
    }

    public String getUserId() {
        return userId;
    }

    public String getChannelId() {
        return channelId;
    }

    public long getTime() {
        return time;
    }

    public String getHumanTime() {
        return humanTime;
    }

    public ReminderType getType() {
        return type;
    }

    public String getReminder() {
        return reminder;
    }

    public void delete() {
        TechDiscordBot.getStorage().deleteReminder(this);
    }

    public void send() {
        User user = TechDiscordBot.getJDA().getUserById(userId);

        if(user != null) {
            ReminderType type = this.type;
            TextChannel channel = TechDiscordBot.getJDA().getTextChannelById(channelId);

            if(channel == null) type = ReminderType.DMs;

            if(type == ReminderType.DMs) {
                try {
                    user.openPrivateChannel().complete().sendMessage("**Reminder**: " + reminder).queue();
                } catch(Exception ex) {
                    if(channel == null)
                        return;

                    sendReminder(user, channel);
                }
            } else {
                sendReminder(user, channel);
            }
        }

        delete();
    }

    private void sendReminder(User user, TextChannel channel) {
        channel.sendMessage("**Reminder for " + user.getAsMention() + "**: " + reminder).queue();
    }
}

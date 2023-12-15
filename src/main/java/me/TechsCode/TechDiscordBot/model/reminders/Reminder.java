package me.techscode.techdiscordbot.model.reminders;

import me.techscode.techdiscordbot.TechDiscordBot;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class Reminder {

	private final String userId, channelId, humanTime, reminder;
	private final long time;
	private final ReminderType type;

	public Reminder(final String userId, final String channelId, final long time, final String humanTime, final ReminderType type, final String reminder) {
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

	/*public void delete() throws SQLException {
		Database.Reminders.remove(this);
	}*/

	public void send() {
		final User user = TechDiscordBot.getJDA().getUserById(userId);

		if (user != null) {
			ReminderType type = this.type;
			final TextChannel channel = TechDiscordBot.getJDA().getTextChannelById(channelId);

			if (channel == null) type = ReminderType.DMs;

			if (type == ReminderType.DMs) {
				try {
					user.openPrivateChannel().queue(msg -> msg.sendMessage("**Reminder**: " + reminder).queue());
				} catch (final Exception ex) {
					if (channel == null)
						return;

					sendReminder(user, channel);
				}
			} else {
				sendReminder(user, channel);
			}
		}

		/*try {
			delete();
		} catch (final SQLException ex) {
			ex.printStackTrace();
		}*/
	}

	private void sendReminder(final User user, final TextChannel channel) {
		channel.sendMessage("**Reminder for " + user.getAsMention() + "**: " + reminder).queue();
	}
}

package me.techscode.techdiscordbot.model.reminders;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ReminderManager {

	private final List<Reminder> reminders = new ArrayList<>();

	// TODO: Fix this part with @Faab as it doesn't work right now!
	public void load() throws SQLException {
		/*final List<Reminder> data = Database.Reminders.get();

		assert data != null;
		this.reminders.addAll(data);*/

		checkForReminders();
	}

	private void checkForReminders() {
		final Thread thread = new Thread(() -> {
			while (true) {
				for (final Reminder reminder : new ArrayList<>(reminders)) {
					if (Math.abs(System.currentTimeMillis() - reminder.getTime()) < 100L) {
						reminder.send();
						reminders.remove(reminder);
					}
				}
			}
		});
		thread.setName("ReminderModule-" + thread.getId());
		thread.start();
	}

	public List<Reminder> getReminders() {
		return this.reminders;
	}

	public List<Reminder> getRemindersByUser(final User user) {
		return this.reminders.stream().filter(reminder -> reminder.getUserId().equals(user.getId())).collect(Collectors.toList());
	}

	public Reminder createReminder(final User user, final String time, final String remind, final TextChannel channel, final Boolean isPrivate) {
		final ReminderArgResponse argResponse = argsToTime(time.split(" "));
		if (argResponse == null) return null;

		if (argResponse.getAmountOfArgs() == 0) {
			return null;
		} else {
			List<String> reminder = new ArrayList<>(Arrays.asList(remind.split(" ")));

			if (reminder.size() == 0) return null;

			final Reminder reminder1 = new Reminder(user.getId(), channel.getId(), argResponse.getTime(), argResponse.getTimeHuman(), (isPrivate ? ReminderType.DMs : ReminderType.CHANNEL), String.join(" ", reminder));
			this.reminders.add(reminder1);

			/*try {
				Database.Reminders.add(reminder1);
			} catch (final SQLException e) {
				e.printStackTrace();
			}*/
			return reminder1;
		}
	}

	public ReminderArgResponse argsToTime(@NotNull final String[] args) {
		final HumanTimeBuilder bhb = new HumanTimeBuilder();
		long time = System.currentTimeMillis();
		int argsAm = 0;
		int i = 0;

		for (final String arg : args) {
			for (final ReminderTimeType rtt : ReminderTimeType.values()) {
				if (Arrays.stream(rtt.getNames()).anyMatch(n -> n.equalsIgnoreCase(arg))) {
					if (i > 0) {
						final String tim = args[i - 1];
						try {
							final int timint = Math.abs(Integer.parseInt(tim));
							time = time + rtt.toMilli(timint);
							bhb.addX(rtt, timint);
							argsAm = argsAm + 2;
						} catch (final Exception e) {
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

	public void deleteReminder(final Reminder reminder) {
		/*try {
			reminder.delete();
		} catch (final SQLException e) {
			e.printStackTrace();
		}*/
		this.reminders.remove(reminder);
	}
}

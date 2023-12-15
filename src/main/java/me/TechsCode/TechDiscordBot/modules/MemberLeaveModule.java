package me.techscode.techdiscordbot.modules;

import com.greazi.discordbotfoundation.Common;
import com.greazi.discordbotfoundation.utils.SimpleEmbedBuilder;
import com.greazi.discordbotfoundation.utils.color.ConsoleColor;
import me.techscode.techdiscordbot.model.Logs;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.utils.TimeUtil;

public class MemberLeaveModule extends ListenerAdapter {

	@SubscribeEvent
	public static void onMemberLeave(final GuildMemberRemoveEvent event) {
		final Thread thread = new Thread(() -> {

			final User user = event.getUser();

			final String name = user.getName();
			final long id = user.getIdLong();

			Common.log(ConsoleColor.CYAN + user.getAsTag() + ConsoleColor.RESET + " has left the server");

			Logs.ServerLogs.log(new SimpleEmbedBuilder("Member left!")
					.field("Name:", name, true)
					.field("ID:", String.valueOf(id), true)
					.field("Account created:", TimeUtil.getDateTimeString(user.getTimeCreated()), true)
					.thumbnail(user.getAvatarUrl())
					.error());
		});
		thread.setName("MemberLeaveModule-" + event.getUser().getId());
		thread.start();
	}
}

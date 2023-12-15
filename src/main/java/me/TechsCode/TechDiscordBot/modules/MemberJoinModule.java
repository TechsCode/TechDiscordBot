package me.techscode.techdiscordbot.modules;

import com.greazi.discordbotfoundation.Common;
import com.greazi.discordbotfoundation.utils.SimpleEmbedBuilder;
import com.greazi.discordbotfoundation.utils.color.ConsoleColor;
import me.techscode.techdiscordbot.database.entities.SqlMember;
import me.techscode.techdiscordbot.model.Logs;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.utils.TimeUtil;

public class MemberJoinModule extends ListenerAdapter {

	@SubscribeEvent
	public static void onMemberJoin(final GuildMemberJoinEvent event) {
		final Thread thread = new Thread(() -> {

			final Member member = event.getMember();

			final String name = member.getEffectiveName();
			final long id = member.getIdLong();

			Common.log(ConsoleColor.CYAN + member.getUser().getAsTag() + ConsoleColor.RESET + " has joined the server");

			new SqlMember(id).save();

			Logs.ServerLogs.log(new SimpleEmbedBuilder("Member joined!")
					.field("Name:", name, true)
					.field("ID:", String.valueOf(id), true)
					.field("Account created:", TimeUtil.getDateTimeString(member.getTimeCreated()), true)
					.thumbnail(member.getUser().getAvatarUrl())
					.success());
		});
		thread.setName("MemberJoinModule-" + event.getUser().getId());
		thread.start();
	}
}

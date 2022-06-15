package me.TechsCode.TechDiscordBot.module.modules;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.module.Module;
import me.TechsCode.TechDiscordBot.objects.Requirement;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;


public class ServerStatusModule extends Module {

	private boolean mentioned = false;

	public ServerStatusModule(TechDiscordBot bot) {
		super(bot);
	}

	@SubscribeEvent
	public void receive(GuildMessageReceivedEvent e) {
		if(!e.getAuthor().getId().equals("984490396799336548")) {
			return;
		}
		if(!e.getMessage().getContentRaw().contains("is now DOWN")){
			return;
		}
		if(mentioned){
			return;
		}
		e.getChannel().sendMessage("<@&774690360836096062>").queue();
		new Thread() {
			public void run() {
				mentioned = true;
				try {
					Thread.sleep(60000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				mentioned = false;
			}
		}.start();
	}

	@Override
	public void onEnable() {}

	@Override
	public void onDisable() {}

	@Override
	public String getName() { return "Developer Tag"; }

	@Override
	public Requirement[] getRequirements() {
		return new Requirement[0];
	}

}

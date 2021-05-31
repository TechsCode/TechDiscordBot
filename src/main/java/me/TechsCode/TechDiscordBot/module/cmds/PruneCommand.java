package me.TechsCode.TechDiscordBot.module.cmds;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.module.CommandModule;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.objects.Query;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import net.dv8tion.jda.api.interactions.components.Button;

public class PruneCommand extends CommandModule {

    private final DefinedQuery<Role> STAFF_ROLE = new DefinedQuery<Role>() {
        @Override
        protected Query<Role> newQuery() { return bot.getRoles("Staff"); }
    };

    public PruneCommand(TechDiscordBot bot) {
        super(bot);
    }

    @Override
    public String getName() {
        return "prune";
    }

    @Override
    public String getDescription() {
        return "Prune messages from this channel.";
    }

    @Override
    public CommandPrivilege[] getCommandPrivileges() {
        return new CommandPrivilege[] { CommandPrivilege.enable(STAFF_ROLE.query().first()) };
    }

    @Override
    public OptionData[] getOptions() {
        return new OptionData[] {
                new OptionData(OptionType.INTEGER, "amount", "The amount of messages to prune. (Default 100)")
        };
    }

    @Override
    public int getCooldown() {
        return 5;
    }

    @Override
    public void onCommand(TextChannel channel, Member m, SlashCommandEvent e) {
        String userId = e.getUser().getId();
        OptionMapping amountOption = e.getOption("amount");

        int amount = amountOption == null ? 100 : (int) Math.min(200, Math.max(2, amountOption.getAsLong()));

        e.reply("**This will delete " + amount + " messages.**\nAre you sure?")
                .addActionRow(
                        Button.success(userId + ":prune:" + amount, "Yes!"),
                        Button.danger(userId + ":delete", "Nevermind!")
                ).queue();
    }

    @SubscribeEvent
    public void onButtonClick(ButtonClickEvent event) {
        String[] id = event.getComponentId().split(":");

        String authorId = id[0];
        String type = id[1];

        if (!authorId.equals(event.getUser().getId()))
            return;

        MessageChannel channel = event.getChannel();
        event.deferEdit().queue();

        switch (type) {
            case "prune":
                int amount = Integer.parseInt(id[2]);
                event.getChannel().getIterableHistory()
                        .skipTo(event.getMessageIdLong())
                        .takeAsync(amount)
                        .thenAccept(channel::purgeMessages);
            case "delete":
                event.getHook().deleteOriginal().queue();
        }
    }
}

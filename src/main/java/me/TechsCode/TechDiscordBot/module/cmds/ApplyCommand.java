package me.TechsCode.TechDiscordBot.module.cmds;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.module.CommandModule;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.objects.Query;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ApplyCommand extends CommandModule {

    private final DefinedQuery<Category> APPLICATION_CATEGORY = new DefinedQuery<Category>() {
        @Override
        protected Query<Category> newQuery() {
            return bot.getCategories("Applications");
        }
    };

    private final DefinedQuery<Role> STAFF_ROLE = new DefinedQuery<Role>() {
        @Override
        protected Query<Role> newQuery() {
            return bot.getRoles("Staff");
        }
    };

    private final DefinedQuery<Role> ASSISTANT_ROLE = new DefinedQuery<Role>() {
        @Override
        protected Query<Role> newQuery() {
            return bot.getRoles("Assistant");
        }
    };


    public ApplyCommand(TechDiscordBot bot) {
        super(bot);
    }

    @Override
    public String getName() {
        return "apply";
    }

    @Override
    public String getDescription() {
        return "Apply to TechsCode Staff.";
    }

    @Override
    public CommandPrivilege[] getCommandPrivileges() {
        return new CommandPrivilege[0];
    }

    @Override
    public OptionData[] getOptions() {
        return new OptionData[0];
    }

    @Override
    public int getCooldown() {
        return 0;
    }

    @Override
    public void onCommand(TextChannel channel, Member m, SlashCommandEvent e) {
        if(getApplyChannel(e.getMember()) != null) {
            new TechEmbedBuilder("Apply Creation - Error")
                    .text("You already have an open application (" + getApplyChannel(e.getMember()).getAsMention() + ")")
                    .error()
                    .sendTemporary(channel, 10);

            return;
        }

        TextChannel applicationChannel = TechDiscordBot.getGuild().createTextChannel("application - " + m.getEffectiveName())
                .setParent(APPLICATION_CATEGORY.query().first())
                .setTopic(m.getAsMention() + "'s Application")
                .complete();

        List<Permission> permissionsAllow = new ArrayList<>(Arrays.asList(Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_READ, Permission.MESSAGE_HISTORY));
        List<Permission> permissionsAllowAssistant = new ArrayList<>(Arrays.asList(Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_ATTACH_FILES, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY));

        applicationChannel.getManager()
                .putPermissionOverride(STAFF_ROLE.query().first(), permissionsAllow, Collections.singletonList(Permission.MESSAGE_TTS))
                .putPermissionOverride(ASSISTANT_ROLE.query().first(), permissionsAllowAssistant, Collections.singleton(Permission.MESSAGE_TTS))
                .putPermissionOverride(m, permissionsAllow, Collections.singletonList(Permission.MESSAGE_TTS))
                .putPermissionOverride(TechDiscordBot.getGuild().getPublicRole(), new ArrayList<>(), Arrays.asList(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE))
                .complete();


        applicationChannel.sendMessage(
                new TechEmbedBuilder(m.getEffectiveName() + "'s Application")
                        .color(Color.CYAN)
                        .text(m.getAsMention() + ", thank you for creating an application. \nFill out these questions and our team will review your application as soon as possible.").build()
        ).queue();
        applicationChannel.sendMessage("**Application Notification:** " + TechDiscordBot.getGuild().getRoleById(608113993038561325l).getAsMention() + ", " + TechDiscordBot.getGuild().getRoleById(311178859171282944l).getAsMention()).queue();

        e.replyEmbeds(
                new TechEmbedBuilder(m.getEffectiveName() + "'s Application Created")
                        .success()
                        .text("Your application has been created at " + applicationChannel.getAsMention())
                        .build()
        ).queue();
    }

    public boolean isApplicationChannel(TextChannel channel) {
        return channel.getName().contains("application-");
    }

    public TextChannel getApplyChannel(Member member) {
        return TechDiscordBot.getGuild().getTextChannels().stream().filter(channel -> isApplicationChannel(channel) && channel.getTopic() != null && channel.getTopic().contains(member.getAsMention())).findFirst().orElse(null);
    }}

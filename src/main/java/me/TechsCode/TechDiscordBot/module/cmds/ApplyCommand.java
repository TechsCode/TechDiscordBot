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
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import net.dv8tion.jda.api.interactions.components.Button;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ApplyCommand extends CommandModule {

    private static boolean isEnabled() {
        return false;
    }

    private final DefinedQuery<Category> APPLICATION_CATEGORY = new DefinedQuery<Category>() {
        @Override
        protected Query<Category> newQuery() {
            return bot.getCategories("Applications");
        }
    };

    private final DefinedQuery<Role> VERIFIED_ROLE = new DefinedQuery<Role>() {
        @Override
        protected Query<Role> newQuery() {
            return bot.getRoles("Verified");
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

    private TextChannel applicationChannel;
    private Member member;
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
        return new CommandPrivilege[] { CommandPrivilege.enable(VERIFIED_ROLE.query().first()) };
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
        if (getApplyChannel(e.getMember()) != null) {
            new TechEmbedBuilder("Apply Creation - Error")
                    .text("You already have an open application (" + getApplyChannel(e.getMember()).getAsMention() + ")")
                    .error()
                    .sendTemporary(channel, 10);

            return;
        } if (!isEnabled()) {
            new TechEmbedBuilder("Apply Creation - Error")
                    .text("Applications have been closed.")
                    .error()
                    .sendTemporary(channel, 10);

            return;
        }

         this.applicationChannel = TechDiscordBot.getGuild().createTextChannel("application - " + m.getEffectiveName())
                .setParent(APPLICATION_CATEGORY.query().first())
                .setTopic(m.getAsMention() + "'s Application")
                .complete();

        applicationChannelPermissions(m);
        applicationProcess(m);

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
    }

    public void applicationChannelPermissions(Member member) {
        List<Permission> permissionsAllow = new ArrayList<>(Arrays.asList(Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_READ, Permission.MESSAGE_HISTORY, Permission.MESSAGE_WRITE));
        List<Permission> permissionsAllowAssistant = new ArrayList<>(Arrays.asList(Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_ATTACH_FILES, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY));

        applicationChannel.getManager()
                .putPermissionOverride(STAFF_ROLE.query().first(), permissionsAllow, Collections.singletonList(Permission.MESSAGE_TTS))
                .putPermissionOverride(ASSISTANT_ROLE.query().first(), permissionsAllowAssistant, Collections.singleton(Permission.MESSAGE_TTS))
                .putPermissionOverride(member, permissionsAllow, Collections.singletonList(Permission.MESSAGE_TTS))
                .putPermissionOverride(TechDiscordBot.getGuild().getPublicRole(), new ArrayList<>(), Arrays.asList(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE))
                .complete();
    }

    public void applicationProcess(Member member) {
        applicationChannel.sendMessage(new TechEmbedBuilder(member.getEffectiveName() + "'s Application")
                .color(Color.CYAN)
                .text(member.getAsMention() + ", thank you for creating an application. \nFill out these questions and our team will review your application as soon as possible.")
                .build()
        ).queue();

        applicationChannel.sendMessage(
                new TechEmbedBuilder("Application Instructions")
                        .color(Color.ORANGE)
                        .text("**Please answer the following questions in a coherent text.**" +
                                "\n**-** How old are you?" +
                                "\n**-** What is your motivation to become a staff member?" +
                                "\n**-** Have you already gained experience as a staff member? If yes, please describe this in detail." +
                                "\n**-** Which TechsCode plugins do you have experience with?")
                        .build()
        ).queue();

        applicationChannel.sendMessage(
                new TechEmbedBuilder("Send Application")
                .success()
                .text("**This will send your application to the staff to review it. **\nAre you sure?")
                .build()).setActionRow(
                    Button.success(member.getId() + ":send:", "Yes!")
        ).queue();

        this.member = member;
    }

    public void applicationNotifications() {
        applicationChannel.sendMessage("**Application Notification:** " + TechDiscordBot.getGuild().getRoleById(608113993038561325l).getAsMention() + ", " + TechDiscordBot.getGuild().getRoleById(311178859171282944l).getAsMention()).queue();
    }

    public void lockApplication() {
        applicationChannel.getManager().removePermissionOverride(member).queue();
        List<Permission> permissionsAllow = new ArrayList<>(Arrays.asList(Permission.MESSAGE_READ, Permission.MESSAGE_HISTORY));

        applicationChannel.getManager()
                .putPermissionOverride(member, permissionsAllow, Collections.singletonList(Permission.MESSAGE_TTS))
                .complete();
    }

    @SubscribeEvent
    public void onButtonClick(ButtonClickEvent event) {
        String[] id = event.getComponentId().split(":");

        String authorId = id[0];
        String type = id[1];

        if (!authorId.equals(event.getUser().getId()))
            return;

        if(type.equalsIgnoreCase("send")) {
            event.getMessage().delete().queue();
            applicationNotifications();
            lockApplication();
        }
    }

}
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
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

public class ApplyCommand extends CommandModule {

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
        return 2;
    }

    @Override
    public void onCommand(TextChannel channel, Member m, SlashCommandEvent e) {
        if (getApplyChannel(e.getMember()) != null) {
            e.replyEmbeds(
                new TechEmbedBuilder("Apply Creation - Error")
                    .text("You already have an open application (" + getApplyChannel(e.getMember()).getAsMention() + ")")
                    .error()
                    .build()
            ).setEphemeral(true).queue();

            return;
        } if (!isEnabled()) {
            e.replyEmbeds(
                new TechEmbedBuilder("Apply Creation - Error")
                    .text("Applications are currently closed, please try again later.")
                    .error()
                    .build()
            ).setEphemeral(true).queue();

            return;
        }

        createApplicationChannel(m, applicationChannel -> {
            applicationChannelPermissions(m, applicationChannel);
            applicationProcess(m, applicationChannel);

            e.replyEmbeds(
                new TechEmbedBuilder(m.getEffectiveName() + "'s Application Created")
                        .success()
                        .text("Your application has been created: " + applicationChannel.getAsMention())
                        .build()
            ).queue();
        });
    }

    public boolean isApplicationChannel(TextChannel channel) {
        return channel.getName().contains("application-");
    }

    public TextChannel getApplyChannel(Member member) {
        return TechDiscordBot.getGuild().getTextChannels().stream().filter(channel -> isApplicationChannel(channel) && channel.getTopic() != null && channel.getTopic().contains(member.getAsMention())).findFirst().orElse(null);
    }

    public void applicationChannelPermissions(Member member, TextChannel channel) {
        List<Permission> permissionsAllow = new ArrayList<>(Arrays.asList(Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_READ, Permission.MESSAGE_HISTORY, Permission.MESSAGE_WRITE));
        List<Permission> permissionsAllowAssistant = new ArrayList<>(Arrays.asList(Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_ATTACH_FILES, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.MESSAGE_HISTORY));
        List<Permission> permissionsDeny = new ArrayList<>(Arrays.asList(Permission.MANAGE_THREADS, Permission.USE_PRIVATE_THREADS, Permission.USE_PUBLIC_THREADS, Permission.MESSAGE_TTS));

        channel.getManager()
                .putPermissionOverride(STAFF_ROLE.query().first(), permissionsAllow, permissionsDeny)
                .putPermissionOverride(ASSISTANT_ROLE.query().first(), permissionsAllowAssistant, permissionsDeny)
                .putPermissionOverride(member, permissionsAllow, permissionsDeny)
                .putPermissionOverride(TechDiscordBot.getGuild().getPublicRole(), new ArrayList<>(), Arrays.asList(Permission.MESSAGE_READ, Permission.MESSAGE_WRITE))
                .queue();
    }

    public void applicationProcess(Member member, TextChannel channel) {
        channel.sendMessageEmbeds(
            new TechEmbedBuilder(member.getEffectiveName() + "'s Application")
                .color(Color.CYAN)
                .text(member.getAsMention() + ", thank you for creating an application.\nFill out these questions and our team will review your application as soon as possible.")
                .build()
        ).queue();

        channel.sendMessageEmbeds(
            new TechEmbedBuilder("Application Instructions")
                .color(Color.ORANGE)
                .text("**Please answer the following questions in a coherent text.**" +
                        "\n**-** How old are you?" +
                        "\n**-** What is your motivation to become a staff member?" +
                        "\n**-** Have you already gained experience as a staff member? If yes, please describe this in detail." +
                        "\n**-** Which TechsCode plugins do you have experience with?")
                .build()
        ).queue();

        channel.sendMessageEmbeds(
            new TechEmbedBuilder("Send Application")
                .success()
                .text("**This will send your application to the staff to review it.**\nAre you sure?")
                .build()
            ).setActionRow(Button.success("apply:" + member.getId() + ":send:", "Yes!")
        ).queue();
    }

    public void applicationNotifications(TextChannel channel) {
        channel.sendMessage("**Application Notification:** " + TechDiscordBot.getGuild().getRoleById(608113993038561325L).getAsMention() + ", " + TechDiscordBot.getGuild().getRoleById(311178859171282944L).getAsMention()).queue();
    }

    public void lockApplication(Member member, TextChannel channel) {
        channel.getManager().removePermissionOverride(member).queue();
        List<Permission> permissionsAllow = new ArrayList<>(Arrays.asList(Permission.MESSAGE_READ, Permission.MESSAGE_HISTORY));

        channel.getManager()
                .putPermissionOverride(member, permissionsAllow, Collections.singletonList(Permission.MESSAGE_TTS))
                .complete();
    }

    public void createApplicationChannel(Member member, Consumer<TextChannel> consumer) {
        String name = "application-" + member.getEffectiveName().replaceAll("[^a-zA-Z\\d\\s_-]", "").toLowerCase();
        if(name.equals("application-"))
            name = "application-" + member.getUser().getId(); //Make sure the ticket has an actual name. In case the regex result is empty.

        TextChannel ticketChannel = TechDiscordBot.getGuild().createTextChannel(name)
                .setParent(APPLICATION_CATEGORY.query().first())
                .setTopic(member.getAsMention() + "'s Application")
                .complete();

        consumer.accept(ticketChannel);
    }

    @SubscribeEvent
    public void onButtonClick(ButtonClickEvent e) {
        if(e.getMember() == null || e.getMessage() == null)
            return;

        String comId = e.getComponentId();
        String[] id = comId.split(":");

        Member member = e.getMember();
        TextChannel channel = getApplyChannel(e.getMember());

        if(comId.startsWith("apply:")) {
            String authorId = id[1];
            String type = id[2];

            if (!authorId.equals(e.getUser().getId()))
                return;

            if (type.equals("send")) {
                e.getMessage().delete().queue();
                //applicationNotifications(channel);
                lockApplication(member, channel);
            }
        }
    }

    private static boolean isEnabled() {
        return false;
    }
}
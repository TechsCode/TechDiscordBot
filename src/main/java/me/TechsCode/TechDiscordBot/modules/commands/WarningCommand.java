package me.TechsCode.TechDiscordBot.modules.commands;

import me.TechsCode.TechDiscordBot.command.CommandModule;
import me.TechsCode.TechDiscordBot.objects.Query;
import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.storage.Warning;
import me.TechsCode.TechDiscordBot.util.CustomEmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.Arrays;
import java.util.Set;

public class WarningCommand extends CommandModule {

    private final DefinedQuery<Role> STAFF_ROLE = new DefinedQuery<Role>() {
        @Override
        protected Query<Role> newQuery() {
            return bot.getRoles("Staff");
        }
    };
    private final DefinedQuery<TextChannel> INFRACTIONS_CHANNEL = new DefinedQuery<TextChannel>() {
        @Override
        protected Query<TextChannel> newQuery() { return TechDiscordBot.getBot().getChannels("infractions"); }
    };

    public WarningCommand(TechDiscordBot bot) { super(bot); }

    @Override
    public String getCommand() { return "!warn"; }

    @Override
    public String[] getAliases() { return new String[]{"!unwarn", "!warnings"}; }

    @Override
    public DefinedQuery<Role> getRestrictedRoles() { return STAFF_ROLE; }

    @Override
    public DefinedQuery<TextChannel> getRestrictedChannels() { return null; }

    @Override
    public void onCommand(TextChannel channel, Message message, Member member, String[] args) {
        if(isWarn(message.getContentDisplay())) {
            if(args.length > 0) {
                Member toWarn = getMemberFromString(message, args[0]);
                if(toWarn == null) {
                    new CustomEmbedBuilder("Warn Command - Error")
                            .setText("Could not find a member using " + args[0] + "!")
                            .error()
                            .sendTemporary(channel, 5);
                    return;
                }
                if(args.length > 1) {
                    String reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                    TechDiscordBot.getBot().getStorage().createWarning(toWarn, member, message, reason);
                    new CustomEmbedBuilder("Warn Command")
                            .setText("Successfully created a warning! You may view it in " + INFRACTIONS_CHANNEL.query().first().getAsMention() + " channel!")
                            .success()
                            .sendTemporary(channel, 5);
                } else {
                    new CustomEmbedBuilder("Warn Command - Error")
                            .setText("Please specify a reason!")
                            .error()
                            .sendTemporary(channel, 5);
                }
            } else {
                new CustomEmbedBuilder("Warn Command - Error")
                        .setText("Please specify a member to warn!")
                        .error()
                        .sendTemporary(channel, 5);
            }
        } else if(isUnWarn(message.getContentDisplay())) {
            if(args.length > 0) {
                try {
                    int warningId = Integer.parseInt(args[0]);
                    Warning warning = TechDiscordBot.getBot().getStorage().retrieveWarnings().stream().filter(w -> w.getId() == warningId).findFirst().orElse(null);
                    if(warning == null) {
                        new CustomEmbedBuilder("Unwarn Command - Error")
                                .setText("Could not find a warning with the id " + warningId + "!")
                                .error()
                                .sendTemporary(channel, 5);
                        return;
                    }
                    warning.getMessage().delete().queue();
                    warning.delete();
                    new CustomEmbedBuilder("Unwarn Command")
                            .setText("Successfully removed the warning with an id " + warningId + "!")
                            .success()
                            .sendTemporary(channel, 5);
                } catch (NumberFormatException ex) {
                    new CustomEmbedBuilder("Unwarn Command - Error")
                            .setText("Cannot turn " + args[1] + "into an integer!")
                            .error()
                            .sendTemporary(channel, 5);
                }
            } else {
                new CustomEmbedBuilder("Unwarn Command - Error")
                        .setText("Please specify a warning id!")
                        .error()
                        .sendTemporary(channel, 5);
            }
        } else if(isWarnings(message.getContentDisplay())) {
            if(args.length > 0) {
                Member warningsMember = getMemberFromString(message, args[0]);
                if(warningsMember == null) {
                    new CustomEmbedBuilder("Warnings Command - Error")
                            .setText("Could not find a member using " + args[0] + "!")
                            .error()
                            .sendTemporary(channel, 5);
                    return;
                }
                Set<Warning> warnings = TechDiscordBot.getBot().getStorage().retrieveWarningsBy(warningsMember);
                if(args.length > 1) {
                    try {
                        int warningId = Integer.parseInt(args[1]);
                        Warning warning = warnings.stream().filter(w -> w.getId() == warningId).findFirst().orElse(null);
                        if(warning == null) {
                            new CustomEmbedBuilder("Warnings Command - Error")
                                    .setText("Could not find a warning with the id " + warningId + "!")
                                    .error()
                                    .sendTemporary(channel, 5);
                            return;
                        }
                        new CustomEmbedBuilder("Warning #" + warningId)
                                .addField("ID", String.valueOf(warningId), true)
                                .addField("Warned Member", warning.getWarned().getAsMention(), true)
                                .addField("Warned By", warning.getWarner().getAsMention(), true)
                                .addField("Warned In", warning.getChannel().getAsMention(), true)
                                .addField("Reason", warning.getReason(), true)
                                .success()
                                .send(channel);
                    } catch (NumberFormatException ex) {
                        new CustomEmbedBuilder("Warnings Command - Error")
                                .setText("Cannot turn " + args[1] + "into an integer!")
                                .error()
                                .sendTemporary(channel, 5);
                    }
                } else {
                    StringBuilder sb = new StringBuilder();
                    int i = 0;
                    for(Warning warning : warnings) {
                        if(i != 0) sb.append("\n");
                        sb.append("`ID: ").append(warning.getId()).append("` Warned by ").append(warning.getWarner().getAsMention());
                        i++;
                    }
                    if(i == 0) sb.append("**All clear!** ").append(warningsMember.getAsMention()).append(" has no warnings!");
                    sb.append("\n\n").append("**Type** `!warnings <user> <id>` **to view more about a specific warning.**");
                    new CustomEmbedBuilder(warningsMember.getEffectiveName() + "'s Warnings")
                            .setText(sb.toString())
                            .success()
                            .send(channel);
                }
            } else {
                new CustomEmbedBuilder("Warnings Command - Error")
                        .setText("Please specify a member to warn!")
                        .error()
                        .sendTemporary(channel, 5);
            }
        }
    }

    public boolean isWarn(String msg) { return msg.startsWith("!warn "); }

    public boolean isUnWarn(String msg) { return msg.startsWith("!unwarn "); }

    public boolean isWarnings(String msg) { return msg.startsWith("!warnings "); }

    public Member getMemberFromString(Message msg, String s) {
        if (msg.getMentionedMembers().size() > 0) {
            return msg.getMentionedMembers().get(0);
        } else if (bot.getGuild().getMembers().stream().anyMatch(mem -> (mem.getUser().getName() + "#" + mem.getUser().getDiscriminator()).equalsIgnoreCase(s) || mem.getUser().getId().equalsIgnoreCase(s))) {
            return bot.getGuild().getMembers().stream().filter(mem -> (mem.getUser().getName() + "#" + mem.getUser().getDiscriminator()).equalsIgnoreCase(s) || mem.getUser().getId().equalsIgnoreCase(s)).findFirst().orElse(null);
        }
        return null;
    }
}

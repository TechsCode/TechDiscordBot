package me.TechsCode.TechDiscordBot.module.modules;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.module.Module;
import me.TechsCode.TechDiscordBot.objects.Requirement;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

public class ReactionModule extends Module {

    public Message reactionMessage;

    private final TextChannel ROLES_CHANNEL = this.bot.getChannel("837679014268895292");

    public ReactionModule(TechDiscordBot bot) {
        super(bot);
    }

    public void onEnable() {
        sendMessage();
        resetReactions();
    }

    public void onDisable() {
        this.reactionMessage.delete().complete();
    }

    public void sendMessage() {
        this.reactionMessage = new TechEmbedBuilder("Reaction Roles")
                .text(getUpdateEmote().getAsMention() + " **For Plugin Updates**\nWhen there is a new update, you will be notified.\n\n" + getAnnouncementEmote().getAsMention() + " **For Announcements**\nWhen there is an announcement, you will receive a ping.\n\n" + getGiveawayEmote().getAsMention() + " **For Giveaways**\nWhen there is a giveaway, you will be notified.")
                .complete(ROLES_CHANNEL);
    }

    public void resetReactions() {
        this.reactionMessage.clearReactions().complete();
        this.reactionMessage.addReaction(getUpdateEmote()).complete();
        this.reactionMessage.addReaction(getAnnouncementEmote()).complete();
        this.reactionMessage.addReaction(getGiveawayEmote()).complete();
    }

    public Emote getAnnouncementEmote() {
        return TechDiscordBot.getJDA().getEmoteById("837724632655986718");
    }

    public Emote getUpdateEmote() {
        return TechDiscordBot.getJDA().getEmoteById("837724632739217418");
    }

    public Emote getGiveawayEmote() {
        return TechDiscordBot.getJDA().getEmoteById("837724632529895486");
    }

    @SubscribeEvent
    public void onReactionAdd(MessageReactionAddEvent e) {
        if (e.getUser() == null || e.getMember() == null) return;
        if (e.getUser().isBot()) return;

        if (e.getMessageId().equals(this.reactionMessage.getId())) {
            if (e.getMember().getRoles().stream().anyMatch(r -> r.getName().equalsIgnoreCase(e.getReactionEmote().getName()))) {
                e.getGuild().removeRoleFromMember(e.getMember(), removeRole(e.getReactionEmote().getName())).queue();
                TechDiscordBot.log("Reaction Role » Removed " + e.getReactionEmote().getName() + " (" + e.getMember().getEffectiveName() + ")");

                new TechEmbedBuilder("Reaction Roles")
                    .text("The {role} Role has been removed, you will not be notified whenever there is a {role}.\nSimply react to add the {role} role again, and it will be added.".replace("{role}", e.getReactionEmote().getName()))
                    .queue(e.getMember());
            } else {
                e.getGuild().addRoleToMember(e.getMember(), giveRole(e.getReactionEmote().getName())).queue();
                TechDiscordBot.log("Reaction Role » Added " + e.getReactionEmote().getName() + " (" + e.getMember().getEffectiveName() + ")");

                new TechEmbedBuilder("Reaction Roles")
                    .text("The {role} Role has been added, you will now be notified whenever there are {role}.\nSimply react to remove the {role} role again, and it will be removed.".replace("{role}", e.getReactionEmote().getName()))
                    .queue(e.getMember());
            }

            e.getReaction().removeReaction(e.getUser()).complete();
            resetReactions();
        }
    }

    public Role removeRole(String role) {
        return TechDiscordBot.getJDA().getGuildById("311178000026566658").getRolesByName(role, true).get(0);
    }

    public Role giveRole(String role) {
        return TechDiscordBot.getJDA().getGuildById("311178000026566658").getRolesByName(role, true).get(0);
    }

    public String getName() {
        return null;
    }

    public Requirement[] getRequirements() {
        return new Requirement[0];
    }
}

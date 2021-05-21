package me.TechsCode.TechDiscordBot.module.modules;

import java.util.ArrayList;
import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.module.Module;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.objects.Query;
import me.TechsCode.TechDiscordBot.objects.Requirement;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

public class ReactionModule extends Module {
  private final DefinedQuery<TextChannel> react_CHANNEL;
  
  private ArrayList<String> Roles;
  
  public Message reactionMessage;
  
  public ReactionModule(TechDiscordBot bot) {
    super(bot);
    this.react_CHANNEL = new DefinedQuery<TextChannel>() {
        protected Query<TextChannel> newQuery() {
          return (Query<TextChannel>)ReactionModule.this.bot.getChannels(new String[] { "react" });
        }
      };
    this.Roles = new ArrayList<>();
  }
  
  public void onEnable() {
    reaction();
    resetreactions();
  }
  
  public void onDisable() {
    this.reactionMessage.delete();
  }
  
  public void reaction() {
    this
      
      .reactionMessage = (new TechEmbedBuilder("Reaction Roles")).setText(":Ename: For plugin updates\n".replace(":Ename:", update().getAsMention()) + "When there is a new update, you will be notified.\n\n" + ":Ename: for announcement\n".replace(":Ename:", announcement().getAsMention()) + "When there is an announcement, you will receive a ping.\n\n" + ":Ename: for giveaways\n".replace(":Ename:", giveaway().getAsMention()) + "When there is a giveaway, you will be notified.\n\n" + ":Ename: for nathan's pings\n".replace(":Ename:", nathan().getAsMention()) + "Get ping for quick giveaways, announcements, and Nathan's messages.").send((TextChannel)this.react_CHANNEL.query().first());
  }
  
  public void resetreactions() {
    this.reactionMessage.addReaction(update()).complete();
    this.reactionMessage.addReaction(announcement()).complete();
    this.reactionMessage.addReaction(giveaway()).complete();
    this.reactionMessage.addReaction(nathan()).complete();
  }
  
  public Emote announcement() {
    return TechDiscordBot.getJDA().getEmoteById("837724632655986718");
  }
  
  public Emote update() {
    return TechDiscordBot.getJDA().getEmoteById("837724632739217418");
  }
  
  public Emote nathan() {
    return TechDiscordBot.getJDA().getEmoteById("837724632672239646");
  }
  
  public Emote giveaway() {
    return TechDiscordBot.getJDA().getEmoteById("837724632529895486");
  }
  
  @SubscribeEvent
  public void onReactionAdd(MessageReactionAddEvent e) {
    if (e.getUser() == null || e.getMember() == null)
      return; 
    if (e.getUser().isBot())
      return; 
    if (e.getMessageId().equals(this.reactionMessage.getId())) {
      if (this.Roles.contains(e.getReactionEmote().getName()))
        if (e.getMember().getRoles().stream().anyMatch(r -> r.getName().equalsIgnoreCase(e.getReactionEmote().getName()))) {
          e.getGuild().removeRoleFromMember(e.getMember(), RemoveRole(e.getReactionEmote().getName())).queue();
          (new TechEmbedBuilder("Reaction Roles"))
            .setText("The {Role} has been removed, you will not be notified whenever there is a {Role}.\nSimply react to add the {Role} role again, and it will be added.".replace("{Role}", e.getReactionEmote().getName())).send(e.getMember());
        } else {
          e.getGuild().addRoleToMember(e.getMember(), GiveRole(e.getReactionEmote().getName())).queue();
          (new TechEmbedBuilder("Reaction Roles"))
            .setText("The {Role} has been added, you will now be notified whenever there are {Role}.\nSimply react to remove the {Role} role again, and it will be removed.".replace("{Role}", e.getReactionEmote().getName())).send(e.getMember());
        }  
      e.getReaction().removeReaction(e.getUser()).complete();
      resetreactions();
    } 
  }
  
  public Role RemoveRole(String Role) {
    return TechDiscordBot.getJDA().getGuildById("311178000026566658").getRolesByName(Role, true).get(0);
  }
  
  public Role GiveRole(String Role) {
    return TechDiscordBot.getJDA().getGuildById("311178000026566658").getRolesByName(Role, true).get(0);
  }
  
  public String getName() {
    return null;
  }
  
  public Requirement[] getRequirements() {
    return new Requirement[0];
  }
}

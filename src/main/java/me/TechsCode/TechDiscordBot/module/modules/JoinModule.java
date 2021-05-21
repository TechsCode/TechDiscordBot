package me.TechsCode.TechDiscordBot.module.modules;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.module.Module;
import me.TechsCode.TechDiscordBot.mysql.storage.Verification;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.objects.Query;
import me.TechsCode.TechDiscordBot.objects.Requirement;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

public class JoinModule extends Module {
  private final DefinedQuery<Role> MUTED_ROLE;
  
  public JoinModule(TechDiscordBot bot) {
    super(bot);
    this.MUTED_ROLE = new DefinedQuery<Role>() {
        protected Query<Role> newQuery() {
          return JoinModule.this.bot.getRoles(new String[] { "Muted" });
        }
      };
  }
  
  public void onEnable() {}
  
  public void onDisable() {}
  
  public String getName() {
    return "JoinModule";
  }
  
  @SubscribeEvent
  public void onGuildMemberJoin(GuildMemberJoinEvent event) {
    Verification existingVerification = TechDiscordBot.getStorage().retrieveVerificationWithDiscord(event.getMember().getId());
    if (event.getMember().getId().equals(Long.valueOf(547187087770648596L)))
      event.getGuild().addRoleToMember(event.getMember(), (Role)this.MUTED_ROLE.query().first()).queue(); 
    if (existingVerification != null) {
      (new TechEmbedBuilder("Welcome to Tech's Support Server"))
        .setText("Hello there,\nYou have previously verified yourself, and your roles will be automatically updated as a result.\nThis update may take 10 to 15 minutes to complete.\n")
        
        .send(event.getMember());
      return;
    } 
    (new TechEmbedBuilder("Welcome to Tech's Support Server"))
      .setText("Welcome to Tech's support server,\nIf you are looking for help, you must first verify your spigot acount in <#695493411117072425>.\nAfter you have been verified, you will have access to the support channel(s).\n")
      
      .send(event.getMember());
  }
  
  public Requirement[] getRequirements() {
    return new Requirement[0];
  }
}

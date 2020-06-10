package me.TechsCode.TechDiscordBot.module.cmds;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.module.CommandCategory;
import me.TechsCode.TechDiscordBot.module.CommandModule;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

public class AternosCommand extends CommandModule {

    public AternosCommand(TechDiscordBot bot) {
        super(bot);
    }

    @Override
    public String getCommand() {
        return "!aternos";
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public DefinedQuery<Role> getRestrictedRoles() {
        return null;
    }

    @Override
    public DefinedQuery<TextChannel> getRestrictedChannels() {
        return null;
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.INFO;
    }

    @Override
    public void onCommand(TextChannel channel, Message message, Member member, String[] args) {
        String general = bot.getChannel("311178000026566658").getAsMention();
        String ping = bot.getMember("714663951320744037").getAsMention();

        new TechEmbedBuilder("Aternos")
                .setText("Our partnership with Aternos has brought tons of people asking for support. **We do not offer support for Aternos members**. It is up to them to supply support for these plugins. Please ping " + ping + " in " + general + " for any questions regarding this.\n" +
                        "\n" +
                        "Thank you for understanding.")
                .setFooter("Command Sent by " + member.getEffectiveName() + "#" + member.getUser().getDiscriminator())
                .send(channel);
    }
}

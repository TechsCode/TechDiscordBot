package me.TechsCode.TechDiscordBot.module.cmds;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.github.GitHubUtil;
import me.TechsCode.TechDiscordBot.github.GithubRelease;
import me.TechsCode.TechDiscordBot.module.CommandCategory;
import me.TechsCode.TechDiscordBot.module.CommandModule;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.objects.Query;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

public class GetReleaseCommand extends CommandModule {

    private final DefinedQuery<Role> STAFF_ROLE = new DefinedQuery<Role>() {
        @Override
        protected Query<Role> newQuery() { return bot.getRoles("Staff"); }
    };

    public GetReleaseCommand(TechDiscordBot bot) {
        super(bot);
    }

    @Override
    public String getCommand() {
        return "!getrelease";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"!release"};
    }

    @Override
    public DefinedQuery<Role> getRestrictedRoles() {
        return STAFF_ROLE;
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
        if(args.length > 0) {
            GithubRelease release = GitHubUtil.getLatestRelease(args[0]);
            if (release == null) {
                channel.sendMessage("**Failed!** Could not get the release!\n\n**Possible reasons:**\n- The repo isn't valid.\n- There is no release in the reop.\n- Github died.").complete();
            } else if (release.getFile() != null) {
                new TechEmbedBuilder(release.getRelease().getName())
                    .setText("```" + (release.getRelease().getBody().isEmpty() ? "No changes specified." : release.getRelease().getBody().replaceAll(" \\|\\| ", "\n")) + "```")
                    .send(channel);


                channel.sendFile(release.getFile(), args[0] + ".jar").complete();

                release.getFile().delete();
            } else {
                channel.sendMessage("**Failed!** Could not get the file!\n\n**Possible reasons:**\n- Eazy fucked up.\n- The release has no files for some reason.\n- GitHub died.").complete();
            }
        } else {
            new TechEmbedBuilder("Error - Get Release")
                    .setText("Please specify a github repo!")
                    .send(channel);
        }
    }

    @Override
    public int getCooldown() {
        return 4;
    }
}

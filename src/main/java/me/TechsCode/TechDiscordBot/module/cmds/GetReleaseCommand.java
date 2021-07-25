package me.TechsCode.TechDiscordBot.module.cmds;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.github.GitHubUtil;
import me.TechsCode.TechDiscordBot.github.GithubRelease;
import me.TechsCode.TechDiscordBot.module.CommandModule;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.objects.Query;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

import java.util.stream.Collectors;

public class GetReleaseCommand extends CommandModule {

    private final DefinedQuery<Role> SUPPORT_ROLES = new DefinedQuery<Role>() {
        @Override
        protected Query<Role> newQuery() { return bot.getRoles("Supporter", "Senior Supporter", "Assistant", "Developer", "\uD83D\uDCBB Coding Wizard"); }
    };

    private final DefinedQuery<Category> SUPPORT_CATEGORIES = new DefinedQuery<Category>() {
        @Override
        protected Query<Category> newQuery() { return bot.getCategories("tickets", "paid plugin support"); }
    };

    public GetReleaseCommand(TechDiscordBot bot) {
        super(bot);
    }

    @Override
    public String getName() {
        return "release";
    }

    @Override
    public String getDescription() {
        return "Get a plugin's latest GitHub jar file.";
    }

    @Override
    public CommandPrivilege[] getCommandPrivileges() {
        return new CommandPrivilege[] { CommandPrivilege.enable(SUPPORT_ROLES.query().first()) };
    }

    @Override
    public OptionData[] getOptions() {
        return new OptionData[] {
                new OptionData(OptionType.STRING, "plugin", "The plugin name.", true)
                    .addChoices(TechDiscordBot.getSpigotAPI().getResources().stream().map(r -> new Command.Choice(r.getName().replace(" ", ""), r.getName().replace(" ", ""))).collect(Collectors.toList()))
        };
    }

    @Override
    public int getCooldown() {
        return 4;
    }

    @Override
    public boolean isHook() {
        return true;
    }

    @Override
    public void onCommand(TextChannel channel, Member m, SlashCommandEvent e) {
        String plugin = e.getOption("plugin").getAsString();

        if (channel.getParent() == SUPPORT_CATEGORIES.query().first()) {
            e.reply("Getting release... please wait.").queue(q -> {
                GithubRelease release = GitHubUtil.getLatestRelease(plugin);

                if (release == null) {
                    q.editOriginal("**Failed!** Could not get the release!\n\n**Possible reasons:**\n- The repo isn't valid.\n- There is no release in the repo.\n- Github died.").queue();
                } else if (release.getFile() != null) {
                    q.editOriginal(release.getFile(), plugin + ".jar")
                            .queue(msg2 -> release.getFile().delete());
                    q.editOriginalEmbeds(
                            new TechEmbedBuilder(release.getRelease().getName())
                                    .text("```" + (release.getRelease().getBody().isEmpty() ? "No changes specified." : release.getRelease().getBody().replaceAll(" \\|\\| ", "\n")) + "```")
                                    .build()
                    ).queue();
                } else {
                    q.editOriginal("**Failed!** Could not get the file!\n\n**Possible reasons:**\n- Eazy messed up.\n- The release has no files for some reason.\n- GitHub died.").queue();
                }
            });
        }else{
            e.replyEmbeds(
                    new TechEmbedBuilder("Get Release - Error")
                            .text("You can not use this command in this channel.\n\n**Available channels:**\n  - Support channels\n  - Tickets")
                            .error()
                            .build()
            ).setEphemeral(true).queue();
        }
    }
}

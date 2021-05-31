package me.TechsCode.TechDiscordBot.module.modules;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.github.GitHubUtil;
import me.TechsCode.TechDiscordBot.github.GithubRelease;
import me.TechsCode.TechDiscordBot.logs.ServerLogs;
import me.TechsCode.TechDiscordBot.module.Module;
import me.TechsCode.TechDiscordBot.objects.Requirement;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import org.apache.commons.text.WordUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class PluginLabModule extends Module {

    private HashMap<String, TextChannel> plugins = new HashMap<>();

    private final HashMap<String, Long> lastUpload = new HashMap<>();

    public PluginLabModule(TechDiscordBot bot) {
        super(bot);
    }

    public HashMap<String, TextChannel> updatePlugins() {
        plugins = new HashMap<>();

        Objects.requireNonNull(TechDiscordBot.getJDA().getCategoryById("741760152570691665")).getTextChannels().stream().filter(Objects::nonNull).filter(channel -> channel.getName().endsWith("-lab")).forEach(channel -> {
            String pluginName;

            if(channel.getTopic() != null) {
                pluginName = channel.getTopic().replace(" Lab", "").replace(" FREE", "");
            } else {
                pluginName = channel.getName().replace("-lab", "").replace("-", "");
            }

            plugins.put(pluginName, channel);
        });

        return plugins;
    }

    @Override
    public void onEnable() {
        new Thread(() -> {
            while (true) {
                updatePlugins();
                detectRelease();

                try {
                    Thread.sleep(TimeUnit.MINUTES.toMillis(2)); //Wait every 2 mins
                } catch (Exception ignored) {}
            }
        }).start();

        new Thread(() -> {
            while (true) {
                updatePermissions();

                try {
                    Thread.sleep(TimeUnit.SECONDS.toMillis(10)); //Wait every 10 seconds
                } catch (Exception ignored) {}
            }
        }).start();
    }

    public void updatePermissions() {
        for(Member member : TechDiscordBot.getGuild().getMembers()) {
            List<String> roles = member.getRoles().stream().map(Role::getName).collect(Collectors.toList());
            if(roles.contains("Staff")) continue; //Skip staff.
            if(!roles.contains("Verified")) continue; //Skip non-verified users.

            plugins.forEach((plugin, channel) -> {
                PermissionOverride permOv = channel.getPermissionOverride(member);
                boolean canSeeChannel = false;
                if(permOv != null) canSeeChannel = permOv.getAllowed().size() > 0 && permOv.getDenied().size() == 0;

                if(roles.contains("Plugin Lab") && (channel.getTopic() != null && channel.getTopic().endsWith("Lab FREE") || roles.contains(plugin)) && !canSeeChannel) {
                    Collection<Permission> permissions = new ArrayList<>(Arrays.asList(Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_READ, Permission.MESSAGE_HISTORY, Permission.MESSAGE_WRITE, Permission.MESSAGE_ATTACH_FILES, Permission.MESSAGE_EMBED_LINKS));
                    channel.getManager().putPermissionOverride(member, permissions, new ArrayList<>()).queue();

                    TechDiscordBot.log("Plugin Lab » Added " + member.getEffectiveName() + " to the " + plugin + "'s Lab.");
                    ServerLogs.log("Plugin Lab", "Added " + member.getAsMention() + " to the " + plugin + "'s Lab.");
                } else if(!roles.contains("Plugin Lab") && canSeeChannel) {
                    channel.getManager().removePermissionOverride(member).queue();

                    TechDiscordBot.log("Plugin Lab » Removed " + plugin + " Lab from (" + member.getEffectiveName() + ")");
                    ServerLogs.log("Plugin Lab", "Removed " + plugin + " Lab from (" + member.getAsMention() + ")");
                }
            });
        }
    }

    public void detectRelease() {
        plugins.forEach((name, channel) -> {
            String plugin = name.replace(" ", "");

            GithubRelease release = GitHubUtil.getLatestRelease(plugin);

            if(release != null) {
                try {
                    Date date = release.getRelease().getCreatedAt();

                    if (lastUpload.containsKey(plugin)) {
                        long lastTime = lastUpload.get(plugin);
                        if (lastTime != date.getTime()) {
                            lastUpload.put(plugin, date.getTime());
                            uploadFile(channel, release, plugin);
                        }
                    } else {
                        lastUpload.put(plugin, date.getTime());
                    }
                } catch (IOException ex) {
                    TechDiscordBot.log("Could not get the last release date from the " + plugin + " repo!");
                }
            } else {
                TechDiscordBot.log("Could not get info from the " + plugin + " repo!");
            }
        });
    }

    public void uploadFile(TextChannel channel, GithubRelease release, String pluginName) {
        try {
            List<Message> msgs = channel.getHistory().retrieveFuture(100).complete().stream().filter(msg -> msg.getAuthor().isBot() && msg.isPinned()).collect(Collectors.toList());
            msgs.forEach(msg -> msg.unpin().queue());
        } catch (IllegalStateException ignored){}

        if(release.getAsset() != null && release.getRelease() != null && release.getFile() != null) {
            new TechEmbedBuilder("Ready to Test: " + WordUtils.capitalize(release.getRelease().getName().replace(".jar", "")))
                    .setText("```" + (release.getRelease().getBody().isEmpty() ? "No changes specified." : release.getRelease().getBody().replaceAll(" \\|\\| ", "\n")) + "```")
                    .send(channel);

            channel.sendFile(release.getFile(), pluginName + ".jar").queueAfter(3, TimeUnit.SECONDS, (msg) -> {
                msg.pin().queue();
                release.getFile().delete();
            });
        }
    }

    @Override
    public void onDisable() {}

    @Override
    public String getName() {
        return "Plugin Lab";
    }

    @Override
    public Requirement[] getRequirements() {
        return new Requirement[0];
    }
}

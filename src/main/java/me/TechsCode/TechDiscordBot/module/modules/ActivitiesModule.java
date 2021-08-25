package me.TechsCode.TechDiscordBot.module.modules;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.module.Module;
import me.TechsCode.TechDiscordBot.mysql.storage.Verification;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.objects.Query;
import me.TechsCode.TechDiscordBot.objects.Requirement;
import me.TechsCode.TechDiscordBot.spigotmc.data.Review;
import me.TechsCode.TechDiscordBot.spigotmc.data.Update;
import me.TechsCode.TechDiscordBot.util.Plugin;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class ActivitiesModule extends Module {

    private final DefinedQuery<Role> UPDATES_ROLE = new DefinedQuery<Role>() {
        protected Query<Role> newQuery() {
            return bot.getRoles("Updates");
        }
    };
    private final DefinedQuery<TextChannel> ACTIVITIES_CHANNEL = new DefinedQuery<TextChannel>() {
        @Override
        protected Query<TextChannel> newQuery() {
            return bot.getChannels("\uD83D\uDCDA︱activities");
        }
    };

    private ArrayList<String> announcedIds;

    public ActivitiesModule(TechDiscordBot bot) {
        super(bot);
    }

    @Override
    public void onEnable() {
        new Thread(() -> {
            while (true) {
                if(!TechDiscordBot.getBot().getSpigotStatus().isUsable())
                    continue;

                TechDiscordBot.getSpigotAPI().getSpigotResources().forEach(resource -> {
                    Plugin plugin = Plugin.fromId(resource.getId());
                    if (plugin == null)
                        return;

                    Update latestUpdate = plugin.getLatestUpdate();

                    boolean isNewUpdate = TechDiscordBot.getStorage().isNewUpdate(resource.getId(), latestUpdate.getId());
                    if(isNewUpdate){
                        printUpdate(plugin, latestUpdate);
                    }
                });

                try {
                    Thread.sleep(TimeUnit.MINUTES.toMillis(5));
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onDisable() {}

    @Override
    public String getName() {
        return "Activities Channel";
    }

    @Override
    public Requirement[] getRequirements() {
        return new Requirement[]{
                new Requirement(ACTIVITIES_CHANNEL, 1, "Missing Activities Channel (#activities)")
        };
    }

    public void printReview(Plugin plugin, Review review) {
        StringBuilder ratingSB = new StringBuilder();
        for(int i = 0; i < review.getRating(); ++i) ratingSB.append(":star:");
        Verification verification = TechDiscordBot.getStorage().retrieveVerificationWithSpigot(review.getUser().getUserId());

        boolean isVerified = verification != null;
        boolean isMemberInDiscord = (isVerified && TechDiscordBot.getBot().getMember(verification.getDiscordId()) != null);
        String usernameOrAt = (isMemberInDiscord ? TechDiscordBot.getBot().getMember(verification.getDiscordId()).getAsMention() : review.getUser().getUsername());

        new TechEmbedBuilder("Review from " + review.getUser().getUsername())
                .color(plugin.getColor())
                .thumbnail(review.getResource().getIcon())
                .field("Rating", ratingSB.toString(), true)
                .text("```" + review.getText() + "```\nThanks to " + usernameOrAt + " for making this review!")
                .queue(ACTIVITIES_CHANNEL.query().first());
        announcedIds.add(review.getResource().getId() + "-" + review.getUser().getUserId());
    }

    public void printUpdate(Plugin plugin, Update update) {
        TechEmbedBuilder ceb = new TechEmbedBuilder("Update for " + update.getResource().getName())
                .color(plugin.getColor())
                .thumbnail(plugin.getResourceLogo());
        ceb.field("Version", update.getResource().getVersion(), true);
        ceb.field("Download", "[Click Here](https://www.spigotmc.org/resources/" + update.getResourceId() + "/update?update=" + update.getId() + ")", true);
        ceb.text((update.getDescription().trim().length() > 0 ? update.getTitle() + "```" + update.getDescription() + "```" : update.getTitle()));

        plugin.getChannel().ifPresent(channel -> {
            new TechEmbedBuilder().text("There's a new update for " + plugin.getEmoji().getAsMention() + " "  + plugin.getRoleName() + "! Make sure to download it [here](https://www.spigotmc.org/resources/" + update.getResourceId() + "/update?update=" + update.getId() + ")!" +
                    "\n\nCheck out " + ACTIVITIES_CHANNEL.query().first().getAsMention() + " for more info!").queue(channel);
        });

        if(update.getImages() != null && update.getImages().length > 0 && !update.getImages()[0].isEmpty()) ceb.image(update.getImages()[0]);
        ceb.queue(ACTIVITIES_CHANNEL.query().first());
        announcedIds.add((update.getId()));
    }
}

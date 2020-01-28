package me.TechsCode.TechDiscordBot.modules;

import com.techeazy.spigotapi.data.objects.*;
import me.TechsCode.TechDiscordBot.Module;
import me.TechsCode.TechDiscordBot.Query;
import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.objects.Requirement;
import me.TechsCode.TechDiscordBot.storage.Verification;
import me.TechsCode.TechDiscordBot.util.CustomEmbedBuilder;
import me.TechsCode.TechDiscordBot.util.Plugin;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.ArrayList;
import java.util.Arrays;

public class ActivitiesChannel extends Module {

    private final DefinedQuery<TextChannel> ACTIVITIES_CHANNEL = new DefinedQuery<TextChannel>() {
        @Override
        protected Query<TextChannel> newQuery() {
            return bot.getChannels("activities");
        }
    };

    private ArrayList<String> announcedIds;

    public ActivitiesChannel(TechDiscordBot bot) {
        super(bot);
    }

    @Override
    public void onEnable() {
        announcedIds = new ArrayList<>();
        new Thread(() -> {
            while (true) {
                if(!bot.getSpigotAPI().isAvailable()) continue;
                if(announcedIds.isEmpty()) {
                    bot.getSpigotAPI().getReviews().getStream().forEach(x -> announcedIds.add(x.getReviewId()));
                    bot.getSpigotAPI().getUpdates().getStream().forEach(x -> announcedIds.add(x.getUpdateId()));
                }
                for(Resource resource : bot.getSpigotAPI().getResources().get()) {
                    Plugin plugin = Plugin.fromId(resource.getResourceId());
                    if(plugin == null) continue;
                    Review[] newReviews = resource.getReviews().getStream().filter(x -> !announcedIds.contains(x.getReviewId())).toArray(Review[]::new);
                    Update[] newUpdates = resource.getUpdates().getStream().filter(x -> !announcedIds.contains(x.getUpdateId())).toArray(Update[]::new);
                    Arrays.stream(newReviews).forEach(review -> printReview(plugin, review));
                    Arrays.stream(newUpdates).forEach(update -> printUpdate(plugin, update));
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
        Verification verification = TechDiscordBot.getBot().getStorage().retrieveVerificationWithSpigot(review.getUserId());
        boolean isVerified = verification != null;
        boolean isMemberInDiscord = (isVerified && TechDiscordBot.getBot().getMember(verification.getDiscordId()) != null);
        String usernameOrAt = (isMemberInDiscord ? TechDiscordBot.getBot().getMember(verification.getDiscordId()).getAsMention() : review.getUsername());
        new CustomEmbedBuilder("Review from " + review.getUsername())
                .setColor(plugin.getColor())
                .setThumbnail(review.getResource().getIcon())
                .addField("Rating", ratingSB.toString(), true)
                .setText("```" + review.getText() + "```\nThanks to " + usernameOrAt + " for making this review!")
                .send(ACTIVITIES_CHANNEL.query().first());
        announcedIds.add((review.getReviewId()));
    }

    public void printUpdate(Plugin plugin, Update update) {
        CustomEmbedBuilder ceb = new CustomEmbedBuilder("Update for " + update.getResourceName())
                .setColor(plugin.getColor())
                .setThumbnail(plugin.getResourceLogo());
        ceb.addField("Version", update.getResource().getVersion(), true);
        ceb.addField("Download", "[Click Here](https://www.spigotmc.org/resources/" + update.getResourceId() + "/update?update=" + update.getUpdateId() + ")", true);
        if (update.getDescription().trim().length() > 0) {
            ceb.setText(update.getTitle() + "```" + update.getDescription() + "```");
        } else {
            ceb.setText(update.getTitle());
        }
        if(update.getImages() != null && update.getImages().length > 0 && !update.getImages()[0].getSource().isEmpty()) ceb.setImage(update.getImages()[0].getSource());
        ceb.send(ACTIVITIES_CHANNEL.query().first());
        announcedIds.add((update.getUpdateId()));
    }
}
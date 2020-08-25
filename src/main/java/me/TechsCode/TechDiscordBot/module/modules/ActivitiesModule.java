package me.TechsCode.TechDiscordBot.module.modules;

import me.TechsCode.SpigotAPI.client.objects.Resource;
import me.TechsCode.SpigotAPI.client.objects.Review;
import me.TechsCode.SpigotAPI.client.objects.Update;
import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.module.Module;
import me.TechsCode.TechDiscordBot.mysql.storage.Verification;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.objects.Query;
import me.TechsCode.TechDiscordBot.objects.Requirement;
import me.TechsCode.TechDiscordBot.util.Plugin;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.ArrayList;
import java.util.Arrays;

public class ActivitiesModule extends Module {

    private final DefinedQuery<TextChannel> ACTIVITIES_CHANNEL = new DefinedQuery<TextChannel>() {
        @Override
        protected Query<TextChannel> newQuery() {
            return bot.getChannels("activities");
        }
    };

    private ArrayList<String> announcedIds;

    public ActivitiesModule(TechDiscordBot bot) {
        super(bot);
    }

    @Override
    public void onEnable() {
        announcedIds = new ArrayList<>();
        new Thread(() -> {
            while (true) {
                if(!TechDiscordBot.getSpigotAPI().isAvailable()) continue;
                if(announcedIds.isEmpty()) {
                    TechDiscordBot.getSpigotAPI().getReviews().getStream().forEach(x -> announcedIds.add(x.getId()));
                    TechDiscordBot.getSpigotAPI().getUpdates().getStream().forEach(x -> announcedIds.add(x.getId()));
                }
                for(Resource resource : TechDiscordBot.getSpigotAPI().getResources().get()) {
                    Plugin plugin = Plugin.fromId(resource.getId());
                    if(plugin == null) continue;
                    Review[] newReviews = resource.getReviews().getStream().filter(x -> !announcedIds.contains(x.getId())).toArray(Review[]::new);
                    Update[] newUpdates = resource.getUpdates().getStream().filter(x -> !announcedIds.contains(x.getId())).toArray(Update[]::new);
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
        Verification verification = TechDiscordBot.getStorage().retrieveVerificationWithSpigot(review.getUserId());

        boolean isVerified = verification != null;
        boolean isMemberInDiscord = (isVerified && TechDiscordBot.getBot().getMember(verification.getDiscordId()) != null);
        String usernameOrAt = (isMemberInDiscord ? TechDiscordBot.getBot().getMember(verification.getDiscordId()).getAsMention() : review.getUsername());

        new TechEmbedBuilder("Review from " + review.getUsername())
                .setColor(plugin.getColor())
                .setThumbnail(review.getResource().getIcon())
                .addField("Rating", ratingSB.toString(), true)
                .setText("```" + review.getText() + "```\nThanks to " + usernameOrAt + " for making this review!")
                .send(ACTIVITIES_CHANNEL.query().first());
        announcedIds.add((review.getId()));
    }

    public void printUpdate(Plugin plugin, Update update) {
        TechEmbedBuilder ceb = new TechEmbedBuilder("Update for " + update.getResourceName())
                .setColor(plugin.getColor())
                .setThumbnail(plugin.getResourceLogo());
        ceb.addField("Version", update.getResource().getVersion(), true);
        ceb.addField("Download", "[Click Here](https://www.spigotmc.org/resources/" + update.getResourceId() + "/update?update=" + update.getId() + ")", true);
        ceb.setText(update.getDescription().trim().length() > 0 ? update.getTitle() + "```" + update.getDescription() + "```" : update.getTitle());

        plugin.getChannel().ifPresent(channel -> {
            new TechEmbedBuilder().setText("There's a new update for " + plugin.getEmoji().getAsMention() + " "  + plugin.getRoleName() + "! Make sure to download it [here](https://www.spigotmc.org/resources/" + update.getResourceId() + "/update?update=" + update.getId() + ")!" +
                    "\n\nCheck out " + ACTIVITIES_CHANNEL.query().first().getAsMention() + " for more info!").send(channel);
        });

        if(update.getImages() != null && update.getImages().length > 0 && !update.getImages()[0].isEmpty()) ceb.setImage(update.getImages()[0]);
        ceb.send(ACTIVITIES_CHANNEL.query().first());
        announcedIds.add((update.getId()));
    }
}
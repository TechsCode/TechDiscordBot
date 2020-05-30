package me.TechsCode.TechDiscordBot.module.modules;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.module.Module;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.objects.Query;
import me.TechsCode.TechDiscordBot.objects.Requirement;
import me.TechsCode.TechDiscordBot.util.TechEmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

import java.util.Arrays;
import java.util.HashMap;

public class SupportWrongChannelModule extends Module {

    private final DefinedQuery<TextChannel> GENERAL_CHANNEL = new DefinedQuery<TextChannel>() {
        @Override
        protected Query<TextChannel> newQuery() { return bot.getChannels("general");
        }
    };

    private final DefinedQuery<TextChannel> PLUGIN_DISCUSSION_CHANNEL = new DefinedQuery<TextChannel>() {
        @Override
        protected Query<TextChannel> newQuery() { return bot.getChannels("plugin-discussion"); } };

    private final DefinedQuery<TextChannel> CODING_HELP = new DefinedQuery<TextChannel>() {
        @Override
        protected Query<TextChannel> newQuery() { return bot.getChannels("coding-help"); } };

    public String[] triggerWords = new String[]{"ultra", "how can i", "help me", "youtube bridge", "youtubebridge", "how do i", "how does", "uperms", "ucustomizer", "customizer", "permissions", "regions"};

    public SupportWrongChannelModule(TechDiscordBot bot) {
        super(bot);
    }

    private HashMap<String, String> messages = new HashMap();

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Override
    public String getName() {
        return "Support in Wrong Channel";
    }

    @SubscribeEvent
    public void onMessage(GuildMessageReceivedEvent e) {
        if(e.getMember() == null) return;
        if(e.getAuthor().isBot()) return;
        if(e.getChannel().getId().equals(GENERAL_CHANNEL.query().first().getId()) || e.getChannel().getId().equals(PLUGIN_DISCUSSION_CHANNEL.query().first().getId()) || e.getChannel().getId().equals(CODING_HELP.query().first().getId())) {
            if(Arrays.stream(triggerWords).anyMatch(word -> e.getMessage().getContentDisplay().toLowerCase().contains(word))) {
                triggerMessage(e.getChannel(), e.getMember());
            }
        }
    }

    @SubscribeEvent
    public void onReaction(GuildMessageReactionAddEvent e) {
        if(e.getUser().isBot()) return;
        if(messages.containsKey(e.getMessageId())) {
            String memberId = e.getMember().getId();
            if(messages.get(e.getMessageId()).equals(memberId)) {
                messages.remove(e.getMessageId());
                e.getChannel().deleteMessageById(e.getMessageId()).queue();
            } else {
                e.getReaction().removeReaction(e.getUser()).queue();
            }
        }
    }

    public void triggerMessage(TextChannel channel, Member member) {
        if(messages.containsValue(member.getId())) return;
        TextChannel verificationChannel = bot.getChannel("695493411117072425");
        Message message = new TechEmbedBuilder().setText("**Hello, " + member.getAsMention() + "!** I've detected that you might be trying to get help in this channel! Please verify in " + verificationChannel.getAsMention() + " in order to get help, thanks!\n\n*If you are not trying to get help, you can delete this message by reacting to it!*")
                .error()
                .send(channel);
        message.addReaction("❌").queue();
        messages.put(message.getId(), member.getId());
    }

    @Override
    public Requirement[] getRequirements() {
        return new Requirement[]{
                new Requirement(GENERAL_CHANNEL, 1, "Could not find #general"),
                new Requirement(PLUGIN_DISCUSSION_CHANNEL, 1, "Could not find #plugin-discussion"),
                new Requirement(CODING_HELP, 1, "Could not find #coding-help")
        };
    }
}

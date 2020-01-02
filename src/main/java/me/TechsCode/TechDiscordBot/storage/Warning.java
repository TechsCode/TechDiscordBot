package me.TechsCode.TechDiscordBot.storage;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

public class Warning {

    private Storage storage;

    private String warnedId;
    private String warnerId;
    private String reason;
    private String channelId;
    private String messageId;

    private int id;

    public Warning(Storage storage, int id, String warnedId, String warnerId, String channelId, String messageId, String reason) {
        this.storage = storage;
        this.id = id;
        this.warnedId = warnedId;
        this.warnerId = warnerId;
        this.channelId = channelId;
        this.messageId = messageId;
        this.reason = reason;
    }

    public int getId() { return id; }

    public Member getWarned() { return TechDiscordBot.getBot().getMember(warnedId); }

    public Member getWarner() { return TechDiscordBot.getBot().getMember(warnerId); }

    public TextChannel getChannel() { return TechDiscordBot.getBot().getChannel(channelId); }

    public Message getMessage() {
        if(isMessageDeleted()) return null;
        return TechDiscordBot.getBot().getChannel(channelId).getMessageById(messageId).complete();
    }

    public boolean didWarnerLeave() { return TechDiscordBot.getBot().getMember(warnerId) == null; }

    public boolean didWarnedLeave() { return TechDiscordBot.getBot().getMember(warnedId) == null; }

    public boolean isChannelDeleted() { return TechDiscordBot.getBot().getChannel(channelId) == null; }

    public boolean isMessageDeleted() {
        if(isChannelDeleted()) return true;
        try {
            return TechDiscordBot.getBot().getChannel(channelId).getMessageById(messageId).complete() == null;
        } catch (Exception ex) {
            return true;
        }
    }

    public String getWarnedId() { return warnedId; }

    public String getWarnerId() { return warnerId; }

    public String getChannelId() { return channelId; }

    public String getMessageId() { return messageId; }

    public String getReason() { return reason; }

    public void delete() { storage.removeWarning(this); }
}

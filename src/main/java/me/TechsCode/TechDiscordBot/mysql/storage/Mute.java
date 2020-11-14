package me.TechsCode.TechDiscordBot.mysql.storage;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import net.dv8tion.jda.api.entities.Member;

public class Mute {

    public final int id;
    public final String memberId, reason;
    public final long end;
    public final boolean expired;

    public Mute(int id, String memberId, String reason, long end, boolean expired) {
        this.id = id;
        this.memberId = memberId;
        this.reason = reason;
        this.end = end;
        this.expired = expired;
    }

    public Mute(String memberId, String reason, long end, boolean expired) {
        this.id = -1;
        this.memberId = memberId;
        this.reason = reason;
        this.end = end;
        this.expired = expired;
    }

    public Member getMember() {
        return TechDiscordBot.getGuild().getMemberById(memberId);
    }

    public String getMemberId() {
        return memberId;
    }

    public int getId() {
        return id;
    }

    public long getEnd() {
        return end;
    }

    public boolean doesExpire() {
        return end != -1L;
    }

    public String getReason() {
        return reason;
    }

    public boolean hasReason() {
        return reason != null;
    }

    public boolean isExpired() {
        return expired;
    }
}

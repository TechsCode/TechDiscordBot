package me.TechsCode.TechDiscordBot.mysql.storage;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import net.dv8tion.jda.api.entities.Member;

import java.util.Random;

public class Warning {

    public final int id;
    public final String memberId, reporterId, reason;
    public final long time;

    public Warning(String memberId, String reporterId, String reason, long time) {
        Random rand = new Random();
        this.id = rand.nextInt(25);

        this.memberId = memberId;
        this.reporterId = reporterId;
        this.reason = reason;
        this.time = time;
    }

    public Warning(int id, String memberId, String reporterId, String reason, long time) {
        this.id = id;
        this.memberId = memberId;
        this.reporterId = reporterId;
        this.reason = reason;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public String getMemberId() {
        return memberId;
    }

    public Member getMember() {
        return TechDiscordBot.getGuild().getMemberById(memberId);
    }

    public String getReporterId() {
        return reporterId;
    }

    public Member getReporter() {
        return TechDiscordBot.getGuild().getMemberById(reporterId);
    }

    public String getReason() {
        return reason;
    }

    public boolean hasReason() {
        return reason != null;
    }

    public long getTime() {
        return time;
    }

    public String getTimeFormatted() {
        return "<t:"+time / 1000+":R>";
    }

    public void save(){
        TechDiscordBot.getStorage().addWarning(this);
    }

    public void delete(){
        TechDiscordBot.getStorage().deleteWarning(id);
    }

}
package me.TechsCode.TechDiscordBot.storage;

import me.TechsCode.TechDiscordBot.Query;
import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.objects.DefinedQuery;
import me.TechsCode.TechDiscordBot.util.CustomEmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Storage {

    /*
    You are asking yourself "where are the caching issues?"
    the answer to that is that nothing will be cached.
     */
    private final String VERIFICATIONS_TABLE = "Verifications";

    private final String WARNINGS_TABLE = "Warnings";

    private final DefinedQuery<TextChannel> INFRACTIONS_CHANNEL = new DefinedQuery<TextChannel>() {
        @Override
        protected Query<TextChannel> newQuery() {
            return TechDiscordBot.getBot().getChannels("infractions");
        }
    };

    private MySQL mySQL;

    private boolean enabled;
    private String errorMessage;

    public Storage(String host, String port, String database, String username, String password) {
        this.mySQL = new MySQL(host, port, database, username, password);
        try {
            mySQL.update("CREATE TABLE IF NOT EXISTS " + VERIFICATIONS_TABLE + " (userid VARCHAR(10), discordid VARCHAR(32));");
            mySQL.update("CREATE TABLE IF NOT EXISTS " + WARNINGS_TABLE + " (id int(10), warnedid VARCHAR(32), warnerid VARCHAR(32), channelid VARCHAR(32), messageid VARCHAR(32), reason TEXT, PRIMARY KEY (id));");
            enabled = true;
        } catch (Exception e) {
            this.errorMessage = e.getMessage();
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Set<Verification> retrieveVerifications() {
        Set<Verification> ret = new HashSet<>();
        try {
            Connection connection = mySQL.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Verifications;");
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) ret.add(new Verification(this, rs.getString("userid"), rs.getString("discordid")));
            rs.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public Verification retrieveVerificationWithDiscord(User user) {
        return retrieveVerificationWithDiscord(user.getId());
    }

    public Verification retrieveVerificationWithDiscord(Member member) {
        return retrieveVerificationWithDiscord(member.getUser().getId());
    }

    public Verification retrieveVerificationWithDiscord(String discordId) {
        return retrieveVerifications().stream().filter(verification -> verification.getDiscordId().equals(discordId)).findFirst().orElse(null);
    }

    public Verification retrieveVerificationWithSpigot(String userId) {
        return retrieveVerifications().stream().filter(verification -> verification.getUserId().equals(userId)).findFirst().orElse(null);
    }

    public void createVerification(String userId, String discordId) {
        try {
            mySQL.update("INSERT INTO " + VERIFICATIONS_TABLE + " (userid, discordid) VALUES ('" + userId + "', '" + discordId + "');");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeVerification(Verification verification) {
        try {
            mySQL.update("DELETE FROM " + VERIFICATIONS_TABLE + " WHERE `userid`=" + verification.getUserId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Set<Warning> retrieveWarnings() {
        Set<Warning> ret = new HashSet<>();
        try {
            Connection connection = mySQL.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Warnings;");
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) ret.add(new Warning(this, rs.getInt("id"), rs.getString("warnedid"), rs.getString("warnerid"), rs.getString("channelid"), rs.getString("messageid"), rs.getString("reason")));
            rs.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public Set<Warning> retrieveWarningsBy(Member member) {
        Set<Warning> ret = new HashSet<>();
        try {
            Connection connection = mySQL.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Warnings WHERE `warnedid`='" + member.getUser().getId() + "';");
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) ret.add(new Warning(this, rs.getInt("id"), rs.getString("warnedid"), rs.getString("warnerid"), rs.getString("channelid"), rs.getString("messageid"), rs.getString("reason")));
            rs.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public void createWarning(Member warned, Member warner, Message message, String reason) {
        Message message2 = new CustomEmbedBuilder("Infractions - Warning")
                .addField("Warned Member", warned.getAsMention(), true)
                .addField("Warned By", warner.getAsMention(), true)
                .addField("Warned In", message.getTextChannel().getAsMention(), true)
                .addField("# Of Warnings", String.valueOf(retrieveWarningsBy(warned).size() + 1), true)
                .addField("Reason", reason, true)
        .send(INFRACTIONS_CHANNEL.query().first());
        try {
            mySQL.update("INSERT INTO " + WARNINGS_TABLE + " (id, warnedid, warnerid, channelid, messageid, reason) VALUES (" + getNextId() + ", '" + warned.getUser().getId() + "', '" + warner.getUser().getId() + "', '" + message2.getChannel().getId() + "', '" + message2.getId() + "', '" + reason.replace("'", "''") + "');");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeWarning(Warning warning) {
        try {
            mySQL.update("DELETE FROM " + WARNINGS_TABLE + " WHERE `id`=" + warning.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getNextId() {
        int id = -1;
        try {
            Connection connection = mySQL.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT MAX(id) as 'id' FROM " + WARNINGS_TABLE);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()) id = rs.getInt("id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id + 1;
    }
}

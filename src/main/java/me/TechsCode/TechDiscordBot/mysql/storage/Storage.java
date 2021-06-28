package me.TechsCode.TechDiscordBot.mysql.storage;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import me.TechsCode.TechDiscordBot.mysql.MySQL;
import me.TechsCode.TechDiscordBot.mysql.MySQLSettings;
import me.TechsCode.TechDiscordBot.reminders.Reminder;
import me.TechsCode.TechDiscordBot.reminders.ReminderType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class Storage {

    private final MySQL mysql;
    private boolean connected;

    private final String VERIFICATIONS_TABLE = "Verifications";
    private final String REMINDERS_TABLE = "Reminders";
    private final String MUTES_TABLE = "Mutes";
    private final String SUB_VERIFICATIONS_TABLE = "SubVerifications";

    private Storage(MySQLSettings mySQLSettings) {
        this.connected = false;
        this.mysql = MySQL.of(mySQLSettings);

        createDefault();
    }

    public static Storage of(MySQLSettings mySQLSettings) {
        return new Storage(mySQLSettings);
    }

    public String getLatestErrorMessage() {
        return mysql.getLatestErrorMessage();
    }

    public boolean isConnected() {
        return connected;
    }

    public void createDefault() {
        mysql.update("CREATE TABLE IF NOT EXISTS " + VERIFICATIONS_TABLE + " (userid varchar(10), discordid varchar(32));");
        mysql.update("CREATE TABLE IF NOT EXISTS " + MUTES_TABLE + " (memberId varchar(32), reason longtext, end varchar(32), expired tinyint(1));");
        mysql.update("CREATE TABLE IF NOT EXISTS " + REMINDERS_TABLE + " (user_id varchar(32), channel_id varchar(32), time varchar(32), type tinyint(1), reminder longtext);");
        mysql.update("CREATE TABLE IF NOT EXISTS " + SUB_VERIFICATIONS_TABLE + " (discordId_verified varchar(32), discordId_subVerified varchar(32));");

        this.connected = true;
    }

    //Verification
    public void createVerification(String userId, String discordId) {
        mysql.update("INSERT INTO " + VERIFICATIONS_TABLE + " (userid, discordid) VALUES ('" + userId + "', '" + discordId + "');");
    }

    public void removeVerification(Verification verification) {
        mysql.update("DELETE FROM " + VERIFICATIONS_TABLE + " WHERE `userid`=" + verification.getUserId());
    }

    //Sub Verification
    public void addSubVerification(String discordId_verified, String discordId_subVerified) {
        mysql.update("INSERT INTO " + SUB_VERIFICATIONS_TABLE + " (discordId_verified, discordId_subVerified) VALUES ('" + discordId_verified + "', '" + discordId_subVerified + "');");
    }

    public void removeSubVerification(String discordId_verified) {
        mysql.update("DELETE FROM " + SUB_VERIFICATIONS_TABLE + " WHERE `discordId_verified`=" + discordId_verified);
    }

    public boolean hasSubVerification(String discordId_verified) {
        boolean hasSubVerification = false;

        try {
            Connection connection = mysql.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + SUB_VERIFICATIONS_TABLE + " WHERE `discordId_verified`=" + discordId_verified);

            ResultSet rs = preparedStatement.executeQuery();
            hasSubVerification = rs.next();

            rs.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

       return hasSubVerification;
    }

    public boolean isSubVerifiedUser(String discordId_subVerified) {
        boolean isSubVerifiedUser = false;

        try {
            Connection connection = mysql.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + SUB_VERIFICATIONS_TABLE + " WHERE `discordId_subVerified`=" + discordId_subVerified);

            ResultSet rs = preparedStatement.executeQuery();
            isSubVerifiedUser = rs.next();

            rs.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return isSubVerifiedUser;
    }

    public String getVerifiedIdFromSubVerifiedId(String discordId_subVerified) {
        String pluginHolder = null;

        try {
            Connection connection = mysql.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + SUB_VERIFICATIONS_TABLE + " WHERE `discordId_subVerified`=" + discordId_subVerified);

            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                pluginHolder = rs.getString("discordId_verified");
            }

            rs.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return pluginHolder;
    }

    public String getSubVerifiedIdFromVerifiedId(String discordId_verified) {
        String pluginHolder = null;

        try {
            Connection connection = mysql.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + SUB_VERIFICATIONS_TABLE + " WHERE `discordId_verified`=" + discordId_verified);

            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                pluginHolder = rs.getString("discordId_subVerified");
            }

            rs.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return pluginHolder;
    }

    public Verification retrieveVerificationWithDiscord(User user) { return retrieveVerificationWithDiscord(user.getId()); }

    public Verification retrieveVerificationWithDiscord(Member member) { return retrieveVerificationWithDiscord(member.getUser().getId()); }

    public Verification retrieveVerificationWithDiscord(String discordId) { return retrieveVerifications().stream().filter(verification -> verification.getDiscordId().equals(discordId)).findFirst().orElse(null); }

    public Verification retrieveVerificationWithSpigot(String userId) { return retrieveVerifications().stream().filter(verification -> verification.getUserId().equals(userId)).findFirst().orElse(null); }

    public Set<Verification> retrieveVerifications() {
        Set<Verification> ret = new HashSet<>();

        try {
            Connection connection = mysql.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + VERIFICATIONS_TABLE + ";");

            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next())
                ret.add(new Verification(this, rs.getString("userid"), rs.getString("discordid")));

            rs.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ret;
    }

    //Mutes
    public Set<Mute> getMutes(Member member) {
        Set<Mute> ret = new HashSet<>();

        try {
            Connection connection = mysql.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + MUTES_TABLE + (member == null ? ";" : " WHERE memberId='" + member.getId() + "';"));
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next())
                ret.add(new Mute(rs.getString("memberId"), rs.getString("reason"), Long.parseLong(rs.getString("time")), rs.getBoolean("expired")));

            rs.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ret;
    }

    public Mute muteExpired(Mute mute, boolean expired) {
        if(mute.getId() == -1) return mute;

        mysql.update("UPDATE " + MUTES_TABLE + " SET expired=? WHERE ", expired);

        return new Mute(mute.getId(), mute.getMemberId(), mute.getReason(), mute.getEnd(), expired);
    }

    public Mute uploadMute(Mute mute) {
        int id = getMutes(null).size() + 1;

        mysql.update("INSERT INTO " + MUTES_TABLE + " (memberId, reason, end, expired) VALUES (?, ?, ?, ?);", mute.getMemberId(), mute.getReason(), mute.getEnd(), mute.isExpired());

        return new Mute(id, mute.getMemberId(), mute.getReason(), mute.getEnd(), mute.isExpired());
    }

    //Preorders
    public Set<Preorder> getPreorders(String plugin, boolean all) {
        Set<Preorder> ret = new HashSet<>();

        try {
            Connection connection = mysql.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + plugin.replace(" ", "") + "Preorders;");
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                String transactionId = rs.getString("transactionId");

                if(!transactionId.equals("NONE") || all) {
                    ret.add(new Preorder(plugin, rs.getString("email"), rs.getLong("discordId"), rs.getString("discordName"), transactionId));
                }
            }

            rs.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ret;
    }

    //Reminders
    public Set<Reminder> retrieveReminders() {
        Set<Reminder> ret = new HashSet<>();

        try {
            Connection connection = mysql.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + REMINDERS_TABLE + ";");
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next())
                ret.add(new Reminder(rs.getString("user_id"), rs.getString("channel_id"), Long.parseLong(rs.getString("time")), null, (rs.getInt("type") == 0 ? ReminderType.CHANNEL : ReminderType.DMs), rs.getString("reminder")));

            rs.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ret;
    }

    public void saveReminder(Reminder reminder) {
        mysql.update("INSERT INTO " + REMINDERS_TABLE + " (user_id, channel_id, time, type, reminder, message_id) VALUES ('" + reminder.getUserId() + "', " + (reminder.getChannelId() == null ? "NULL" : "'" + reminder.getChannelId() + "'") + ", '" + reminder.getTime() + "', " + reminder.getType().getI() + ", '" + reminder.getReminder().replace("'", "''") + "', 'XXXX');");
    }

    public void deleteReminder(Reminder reminder) {
        mysql.update("DELETE FROM " + REMINDERS_TABLE + " WHERE user_id='" + reminder.getUserId() + "' AND channel_id='" + (reminder.getChannelId() == null ? "NULL" : "'" + reminder.getChannelId() + "'") + "' AND time='" + reminder.getTime() + "' AND type=" + reminder.getType().getI() + " AND reminder='" + reminder.getReminder().replace("'", "''") + "';");
    }
}

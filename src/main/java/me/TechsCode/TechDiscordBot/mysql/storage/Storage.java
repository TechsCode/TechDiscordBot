package me.TechsCode.TechDiscordBot.mysql.storage;

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
        mysql.update("CREATE TABLE IF NOT EXISTS " + VERIFICATIONS_TABLE + " (userid VARCHAR(10), discordid VARCHAR(32));");
        mysql.update("CREATE TABLE IF NOT EXISTS " + REMINDERS_TABLE + " (user_id varchar(32), channel_id varchar(32), time varchar(32), type tinyint(1), reminder longtext);");

        this.connected = true;
    }

    public void createVerification(String userId, String discordId) {
        mysql.update("INSERT INTO " + VERIFICATIONS_TABLE + " (userid, discordid) VALUES ('" + userId + "', '" + discordId + "');");
    }

    public void removeVerification(Verification verification) {
        mysql.update("DELETE FROM " + VERIFICATIONS_TABLE + " WHERE `userid`=" + verification.getUserId());
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
            while (rs.next()) ret.add(new Verification(this, rs.getString("userid"), rs.getString("discordid")));
            rs.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public Set<Reminder> retrieveReminders() {
        Set<Reminder> ret = new HashSet<>();
        try {
            Connection connection = mysql.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + REMINDERS_TABLE + ";");
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) ret.add(new Reminder(rs.getString("user_id"), rs.getString("channel_id"), Long.parseLong(rs.getString("time")), null, (rs.getInt("type") == 0 ? ReminderType.CHANNEL : ReminderType.DMs), rs.getString("reminder")));
            rs.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public Set<Preorder> getPreorders(String plugin) {
        Set<Preorder> ret = new HashSet<>();
        try {
            Connection connection = mysql.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + plugin.replace(" ", "") + "Preorders;");
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) ret.add(new Preorder(plugin, rs.getString("email"), rs.getLong("discordId"), rs.getString("discordName"), rs.getString("transactionId")));
            rs.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public void saveReminder(Reminder reminder) {
        mysql.update("INSERT INTO " + REMINDERS_TABLE + " (user_id, channel_id, time, type, reminder) VALUES ('" + reminder.getUserId() + "', " + (reminder.getChannelId() == null ? "NULL" : "'" + reminder.getChannelId() + "'") + ", '" + reminder.getTime() + "', " + reminder.getType().getI() + ", '" + reminder.getReminder().replace("'", "''") + "');");
    }

    public void deleteReminder(Reminder reminder) {
        mysql.update("DELETE FROM " + REMINDERS_TABLE + " WHERE user_id='" + reminder.getUserId() + "' AND channel_id=" + (reminder.getChannelId() == null ? "NULL" : "'" + reminder.getChannelId() + "'") + " AND time='" + reminder.getTime() + "' AND type=" + reminder.getType().getI() + " AND reminder='" + reminder.getReminder().replace("'", "''") + "';");
    }
}

package me.TechsCode.TechDiscordBot.mysql.storage;

import me.TechsCode.TechDiscordBot.mysql.MySQL;
import me.TechsCode.TechDiscordBot.mysql.MySQLSettings;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class Storage {

    private MySQL mysql;
    private boolean connected;

    private String VERIFICATIONS_TABLE = "Verifications";

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
}

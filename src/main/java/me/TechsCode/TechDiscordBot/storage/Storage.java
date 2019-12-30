package me.TechsCode.TechDiscordBot.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Storage {

    /*

    You are asking yourself "where are the caching issues?" ..

    the answer to that is that nothing will be cached.

     */
    private final String VERIFICATIONS_TABLE = "Verifications";

    private MySQL mySQL;

    private boolean enabled;
    private String errorMessage;

    public Storage(String host, String port, String database, String username, String password) {
        this.mySQL = new MySQL(host, port, database, username, password);

        try {
            mySQL.update("CREATE TABLE IF NOT EXISTS " + VERIFICATIONS_TABLE + " (userid VARCHAR(10), discordid VARCHAR(32));");
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

            while (rs.next()) {
                ret.add(new Verification(this, rs.getString("userid"), rs.getString("discordid")));
            }

            rs.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ret;
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
}

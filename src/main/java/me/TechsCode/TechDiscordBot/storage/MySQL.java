package me.TechsCode.TechDiscordBot.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MySQL {

    private String host, port, database, username, password;

    public MySQL(String host, String port, String database, String username, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    protected void update(String qry) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement p = connection.prepareStatement(qry);
        p.execute();
        connection.close();
        p.close();
    }

    protected Connection getConnection() throws SQLException {
        String connectString = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&characterEncoding=utf-8";
        return DriverManager.getConnection(connectString, username, password);
    }
}

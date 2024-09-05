package com.goaltracker.services.DBService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

public class DatabaseConnection {

    private static final String FILE_PROPERTIES = "/database.properties";

    private static String dbUrl;
    private static String dbUser;
    private static String dbPassword;

    static {
        try (InputStream input = DatabaseConnection.class.getResourceAsStream(FILE_PROPERTIES)) {
            if (input == null) {
                throw new RuntimeException("Unable to find " + FILE_PROPERTIES);
            }

            Properties props = new Properties();
            props.load(input);
            dbUrl = props.getProperty("db.url");
            dbUser = props.getProperty("db.user");
            dbPassword = props.getProperty("db.password");

        } catch (IOException e) {
            throw new RuntimeException("Failed to load database properties", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found. Please include it in your library path.", e);
        }

        return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }
}

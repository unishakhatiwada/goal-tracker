package Handlers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/goaltracker";

    private static final String DB_USER = "unisha";
    private static final String DB_PASSWORD = "unisha";

    public static Connection getConnection() throws SQLException {
        // Ensure the MySQL JDBC Driver is loaded
        try {
            Class.forName("com.mysql.jdbc.Driver");

        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found. Please include it in your library path.", e);
        }

        // Establish the database connection
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
}

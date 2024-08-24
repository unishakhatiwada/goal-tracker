package Handlers;

import Utils.URIHelper;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.google.gson.Gson;

public class RegisterHandler implements HttpHandler {
    private static final Gson gson = new Gson();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {

            // Parse JSON to User object
            User user = URIHelper.getRequestBody(exchange, User.class);

            // Register user
            try (Connection conn = DatabaseConnection.getConnection()) {
                // Insert user into the database
                String userQuery = "INSERT INTO users (email, password, confirm_password) VALUES (?, ?, ?)";
                try (PreparedStatement userStmt = conn.prepareStatement(userQuery)) {
                    userStmt.setString(1, user.getEmail());
                    userStmt.setString(2, user.getPassword());
                    userStmt.setString(3, user.getConfirmPassword());
                    int affectedRows = userStmt.executeUpdate();

                    if (affectedRows > 0) {
                        // Send success response
                        String response = "{\"message\": \"User registered successfully!\"}";
                        exchange.sendResponseHeaders(200, response.length());
                        OutputStream os = exchange.getResponseBody();
                        os.write(response.getBytes());
                        os.close();
                    } else {
                        // Handle registration failure
                        String response = "Registration failed";
                        exchange.sendResponseHeaders(500, response.length());
                        OutputStream os = exchange.getResponseBody();
                        os.write(response.getBytes());
                        os.close();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                String errorResponse = "Database error: " + e.getMessage();
                exchange.sendResponseHeaders(500, errorResponse.length());
                OutputStream os = exchange.getResponseBody();
                os.write(errorResponse.getBytes());
                os.close();
            }
        } else {
            // Method not allowed
            String response = "Method not allowed";
            exchange.sendResponseHeaders(405, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    // User class for JSON parsing
    static class User {
        private String email;
        private String password;
        private String confirm_password;

        // Getters and setters
        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getConfirmPassword() {
            return confirm_password;
        }

        public void setConfirmPassword(String confirm_password) {
            this.confirm_password = confirm_password;
        }
    }
}

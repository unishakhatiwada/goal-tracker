package Handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class LoginHandler implements HttpHandler {
    private static final Gson gson = new Gson();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            var reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), "utf-8"));
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
            reader.close();
            String json = jsonBuilder.toString();

            // Parse JSON to Login object
            Login login = gson.fromJson(json, Login.class);

            // Validate user
            boolean isValidUser = false;
            try (Connection conn = DatabaseConnection.getConnection()) {
                String query = "SELECT * FROM users WHERE email = ? AND password = ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, login.getEmail());
                    stmt.setString(2, login.getPassword());
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        isValidUser = true;
                    }
                }

                if (isValidUser) {
                    // Generate a token
                    String token = UUID.randomUUID().toString();
                    String tokenQuery = "INSERT INTO tokens (user_id, token) VALUES (?, ?)";
                    try (PreparedStatement stmt = conn.prepareStatement(tokenQuery)) {
                        stmt.setInt(1, getUserIdByEmail(conn, login.getEmail()));
                        stmt.setString(2, token);
                        stmt.executeUpdate();
                    }

                    // Send success response with token
                    String response = gson.toJson(new TokenResponse("Login successful", token));
                    exchange.sendResponseHeaders(200, response.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } else {
                    // Invalid credentials
                    String response = "{\"message\": \"Invalid email or password\"}";
                    exchange.sendResponseHeaders(401, response.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                String errorResponse = "{\"message\": \"Database error: " + e.getMessage() + "\"}";
                exchange.sendResponseHeaders(500, errorResponse.length());
                OutputStream os = exchange.getResponseBody();
                os.write(errorResponse.getBytes());
                os.close();
            }
        } else {
            // Method not allowed
            String response = "{\"message\": \"Method not allowed\"}";
            exchange.sendResponseHeaders(405, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    // Helper method to get user ID by email
    private int getUserIdByEmail(Connection conn, String email) throws SQLException {
        String query = "SELECT id FROM users WHERE email = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        return -1; // User not found
    }

    // Login class for JSON parsing
    static class Login {
        private String email;
        private String password;

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
    }

    // Token response class
    static class TokenResponse {
        private String message;
        private String token;

        public TokenResponse(String message, String token) {
            this.message = message;
            this.token = token;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}

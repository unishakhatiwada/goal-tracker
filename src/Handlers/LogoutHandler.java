package Handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LogoutHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            // Get the token from the Authorization header
            String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                String response = "{\"message\": \"Authorization token is missing.\"}";
                exchange.sendResponseHeaders(401, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
                return;
            }

            String token = authHeader.substring(7); // Extract token after "Bearer "

            try (Connection conn = DatabaseConnection.getConnection()) {
                // Delete the token from the database
                String deleteTokenQuery = "DELETE FROM tokens WHERE token = ?";
                try (PreparedStatement stmt = conn.prepareStatement(deleteTokenQuery)) {
                    stmt.setString(1, token);
                    int affectedRows = stmt.executeUpdate();

                    if (affectedRows > 0) {
                        // Token deleted successfully
                        String response = "{\"message\": \"Logged out successfully.\"}";
                        exchange.sendResponseHeaders(200, response.length());
                        OutputStream os = exchange.getResponseBody();
                        os.write(response.getBytes());
                        os.close();
                    } else {
                        // Token not found
                        String response = "{\"message\": \"Invalid token.\"}";
                        exchange.sendResponseHeaders(401, response.length());
                        OutputStream os = exchange.getResponseBody();
                        os.write(response.getBytes());
                        os.close();
                    }
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
            String response = "{\"message\": \"Method not allowed.\"}";
            exchange.sendResponseHeaders(405, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}

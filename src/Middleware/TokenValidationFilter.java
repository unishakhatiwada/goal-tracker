package Middleware;

import Handlers.DatabaseConnection;
import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TokenValidationFilter extends Filter {

    @Override
    public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
        String requestPath = exchange.getRequestURI().getPath();

        // Skip token validation for login requests
        if ("/login".equals(requestPath)) {
            chain.doFilter(exchange);
            return;
        }

        // Get the Authorization header from the request
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendError(exchange, "Authorization token is missing.");
            return;
        }

        String token = authHeader.substring(7); // Remove "Bearer " from the token

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM tokens WHERE token = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, token);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    // Token is valid, continue with the request
                    chain.doFilter(exchange);
                } else {
                    // Invalid token
                    sendError(exchange, "Invalid token.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            sendError(exchange, "Database error: " + e.getMessage());
        }
    }

    @Override
    public String description() {
        return "Filter to validate authorization tokens";
    }

    private void sendError(HttpExchange exchange, String message) throws IOException {
        String response = "{\"message\": \"" + message + "\"}";
        exchange.sendResponseHeaders(401, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}

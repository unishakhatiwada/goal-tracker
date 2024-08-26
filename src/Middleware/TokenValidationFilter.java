package Middleware;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;
import Handlers.DBService.DatabaseConnection;
import Utils.ResponseHelper;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TokenValidationFilter extends Filter {

    private static final String LOGIN_PATH = "/login";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTH_HEADER = "Authorization";
    private static final int UNAUTHORIZED = 401;

    @Override
    public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
        String requestPath = exchange.getRequestURI().getPath();

        if (LOGIN_PATH.equals(requestPath)) {
            chain.doFilter(exchange);
            return;
        }

        String token = extractToken(exchange);
        if (token == null) {
            ResponseHelper.sendErrorResponse(exchange, UNAUTHORIZED, "Unauthorized");
            return;
        }

        if (isTokenValid(token)) {
            try {

                int userId = getAuthenticatedUserId(token);
                exchange.setAttribute("userId", userId);
                chain.doFilter(exchange);
            } catch (SQLException e) {

                ResponseHelper.sendErrorResponse(exchange, UNAUTHORIZED, "Invalid token.");
            }
        } else {

            ResponseHelper.sendErrorResponse(exchange, UNAUTHORIZED, "Invalid token.");
        }

    }

    @Override
    public String description() {
        return "Filter to validate authorization tokens";
    }

    private String extractToken(HttpExchange exchange) {
        String authHeader = exchange.getRequestHeaders().getFirst(AUTH_HEADER);
        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            return authHeader.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    private boolean isTokenValid(String token) {
        String query = "SELECT * FROM tokens WHERE token = ?";

        try (Connection c = DatabaseConnection.getConnection();
                PreparedStatement stmt = c.prepareStatement(query)) {

            stmt.setString(1, token);

            try (ResultSet rs = stmt.executeQuery()) {

                return rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private int getAuthenticatedUserId(String token) throws SQLException {
        String query = "SELECT user_id FROM tokens WHERE token = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, token);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("user_id");
                } else {
                    throw new SQLException("Token not found.");
                }
            }
        }
    }

    // private void logError(String message, Exception e) {
    //     System.err.println(message + ": " + e.getMessage());
    //     e.printStackTrace();
    // }
}

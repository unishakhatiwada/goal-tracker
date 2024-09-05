package com.goaltracker.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import com.goaltracker.utils.ResponseHelper;
import com.goaltracker.services.DBService.DatabaseConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LogoutHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {

            String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
            String token = authHeader != null ? authHeader.substring(7) : null;

            if (token == null) {
                ResponseHelper.sendErrorResponse(exchange, 401, "Authorization token is missing.");
                return;
            }

            try (Connection conn = DatabaseConnection.getConnection()) {

                String deleteTokenQuery = "DELETE FROM tokens WHERE token = ?";
                try (PreparedStatement stmt = conn.prepareStatement(deleteTokenQuery)) {
                    stmt.setString(1, token);
                    int affectedRows = stmt.executeUpdate();

                    if (affectedRows > 0) {

                        ResponseHelper.sendSuccessResponse(exchange, "Logged out successfully", null);
                    } else {

                        ResponseHelper.sendErrorResponse(exchange, 401, "Invalid token.");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                ResponseHelper.sendErrorResponse(exchange, 500, "Database error: " + e.getMessage());
            }
        } else {

            ResponseHelper.sendErrorResponse(exchange, 405, "Method not allowed.");
        }
    }
}

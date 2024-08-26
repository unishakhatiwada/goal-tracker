package Handlers;

import Utils.ResponseHelper;
import Model.User;
import Utils.URIHelper;
import com.sun.net.httpserver.HttpHandler;
import Handlers.DBService.DatabaseConnection;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegisterHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            try {
                User user = URIHelper.getRequestBody(exchange, User.class);

                if (!isValidRegistration(user, exchange)) {
                    return;
                }

                user.hashPassword();

                try (Connection conn = DatabaseConnection.getConnection()) {
                    String userQuery = "INSERT INTO users (email, password, username) VALUES (?, ?, ?)";
                    try (PreparedStatement userStmt = conn.prepareStatement(userQuery)) {
                        userStmt.setString(1, user.getEmail());
                        userStmt.setString(2, user.getPassword());
                        userStmt.setString(3, user.getUsername());
                        int affectedRows = userStmt.executeUpdate();

                        if (affectedRows > 0) {
                            ResponseHelper.sendSuccessResponse(exchange, "User registered successfully!", null);
                        } else {
                            ResponseHelper.sendErrorResponse(exchange, 500, "Registration failed");
                        }
                    }
                } catch (SQLException e) {
                    if (e.getErrorCode() == 1062) {
                        ResponseHelper.sendErrorResponse(exchange, 409, "This email is already registered.");
                    } else {
                        ResponseHelper.sendErrorResponse(exchange, 500, "Database error: " + e.getMessage());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                ResponseHelper.sendErrorResponse(exchange, 500, "Server error: " + e.getMessage());
            }
        } else {
            ResponseHelper.sendErrorResponse(exchange, 405, "Method not allowed");
        }
    }

    private boolean isValidRegistration(User user, HttpExchange exchange) throws IOException {
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            ResponseHelper.sendErrorResponse(exchange, 400, "Email is required.");
            return false;
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            ResponseHelper.sendErrorResponse(exchange, 400, "Password is required.");
            return false;
        }
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            ResponseHelper.sendErrorResponse(exchange, 400, "Username is required.");
            return false;
        }
        return true;
    }
}

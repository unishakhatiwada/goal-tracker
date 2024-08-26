package Handlers;

import Model.Token;
import Model.User;
import Utils.URIHelper;
import Utils.ResponseHelper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import Handlers.DBService.DatabaseConnection;
// import com.google.gson.Gson;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

public class LoginHandler implements HttpHandler {
    // private static final Gson gson = new Gson();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            User user = URIHelper.getRequestBody(exchange, User.class);

            try (Connection conn = DatabaseConnection.getConnection()) {
                String query = "SELECT * FROM users WHERE email = ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, user.getEmail());
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        String storedPassword = rs.getString("password");
                        try {
                            if (user.verifyPassword(user.getPassword(), storedPassword)) {
                                // Set user data from database
                                user.setId(rs.getInt("id"));
                                user.setEmail(rs.getString("email"));
                                user.setUsername(rs.getString("username"));

                                // Generate and store a token for the user
                                Token tokenObj = new Token();
                                String generatedToken = tokenObj.generateAndStoreToken(conn, user.getId());

                                // Get user data with token
                                Map<String, Object> responseData = user.getUserDataWithToken(generatedToken);

                                // Send success response with token and user data
                                ResponseHelper.sendSuccessResponse(exchange, "Login successful", responseData);
                            } else {
                                ResponseHelper.sendErrorResponse(exchange, 401, "Invalid email or password");
                            }
                        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                            e.printStackTrace();
                            ResponseHelper.sendErrorResponse(exchange, 500,
                                    "Error verifying password: " + e.getMessage());
                        }
                    } else {
                        ResponseHelper.sendErrorResponse(exchange, 401, "Invalid email or password");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                ResponseHelper.sendErrorResponse(exchange, 500, "Database error: " + e.getMessage());
            }
        } else {
            ResponseHelper.sendErrorResponse(exchange, 405, "Method not allowed");
        }
    }
}
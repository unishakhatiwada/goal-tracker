package Handlers.Goals;
import Handlers.DatabaseConnection;
import Utils.ResponseHelper;
import Utils.URIHelper;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.stream.Collectors;

public class UpdateGoalHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("PUT".equals(exchange.getRequestMethod())) {
            int goalId = URIHelper.getId(exchange);
             GoalModel goal=URIHelper.getRequestBody(exchange, GoalModel.class);
            try (Connection conn = DatabaseConnection.getConnection()) {
                String query = "UPDATE goals SET title = ?, purpose = ?, completion = ?, deadline = ? WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setString(1, goal.getTitle());
                    stmt.setString(2, goal.getPurpose());
                    stmt.setBoolean(3, goal.isCompletion());
                    stmt.setObject(4, goal.getDeadline());
                    stmt.setInt(5, goalId);
                    stmt.executeUpdate();

                int rowsAffected = stmt.executeUpdate();

                if (rowsAffected > 0) {
                    String response = "{\"message\": \"Goal updated successfully\"}";
                    exchange.sendResponseHeaders(200, response.length());
                    try (var os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                } else {
                    String response = "{\"message\": \"Goal not found\"}";
                    exchange.sendResponseHeaders(404, response.length());
                    try (var os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
                String response = "{\"message\": \"Server error: " + e.getMessage() + "\"}";
                exchange.sendResponseHeaders(500, response.length());
                try (var os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }
        } else {
            exchange.sendResponseHeaders(405, -1); // Method Not Allowed
        }

    }
}

package Handlers.Goals;

import Handlers.DatabaseConnection;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

public class CreateGoalHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            String requestBody = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))
                    .lines().collect(Collectors.joining("\n"));

            try {
                GoalModel goal = new Gson().fromJson(requestBody, GoalModel.class);

                try (Connection conn = DatabaseConnection.getConnection()) {
                    String query = "INSERT INTO goals (title, purpose, deadline) VALUES (?, ?, ?)";
                    try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                        stmt.setString(1, goal.getTitle());
                        stmt.setString(2, goal.getPurpose());
                        stmt.setObject(3, goal.getDeadlineAsDateTime()); // Use setObject for LocalDateTime
                        stmt.executeUpdate();

                        // Retrieve the generated key (ID) if needed
                        ResultSet generatedKeys = stmt.getGeneratedKeys();
                        if (generatedKeys.next()) {
                            int generatedId = generatedKeys.getInt(1);

                            // Create response JSON object
                            JsonObject responseJson = new JsonObject();
                            responseJson.addProperty("message", "Goal created successfully");

                            JsonObject dataJson = new JsonObject();
                            dataJson.addProperty("id", generatedId);
                            dataJson.addProperty("title", goal.getTitle());
                            dataJson.addProperty("purpose", goal.getPurpose());
                            dataJson.addProperty("completion", goal.isCompletion());
                            dataJson.addProperty("deadline", goal.getDeadline());
                            dataJson.addProperty("createdAt", goal.getCreatedAt());
                            dataJson.addProperty("updatedAt", goal.getUpdatedAt());
                            responseJson.add("data", dataJson);

                            // Send JSON response
                            String response = new Gson().toJson(responseJson);
                            exchange.sendResponseHeaders(201, response.length()); // 201 Created
                            OutputStream os = exchange.getResponseBody();
                            os.write(response.getBytes());
                            os.close();
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    String response = "{\"message\": \"Database error: " + e.getMessage() + "\"}";
                    exchange.sendResponseHeaders(500, response.length());
                    exchange.getResponseBody().write(response.getBytes());
                }
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                String response = "{\"message\": \"Invalid JSON format.\"}";
                exchange.sendResponseHeaders(400, response.length());
                exchange.getResponseBody().write(response.getBytes());
            } catch (Exception e) {
                e.printStackTrace();
                String response = "{\"message\": \"Server error: " + e.getMessage() + "\"}";
                exchange.sendResponseHeaders(500, response.length());
                exchange.getResponseBody().write(response.getBytes());
            } finally {
                exchange.getResponseBody().close();
            }
        } else {
            exchange.sendResponseHeaders(405, -1); // Method Not Allowed
        }
    }
}

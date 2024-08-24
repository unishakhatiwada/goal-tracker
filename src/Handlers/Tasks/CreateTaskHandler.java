package Handlers.Tasks;

import Handlers.DatabaseConnection;
import Utils.URIHelper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateTaskHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            try {
                TaskModel task = URIHelper.getRequestBody(exchange, TaskModel.class);

                try (Connection conn = DatabaseConnection.getConnection()) {
                    String query = "INSERT INTO tasks (goal_id, title, description, status, due_date) VALUES (?, ?, ?, ?, ?)";
                    try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                        stmt.setInt(1, task.getGoalId());
                        stmt.setString(2, task.getTitle());
                        stmt.setString(3, task.getDescription());
                        stmt.setString(4, task.getStatus());
                        stmt.setString(5, task.getDueDate());

                        stmt.executeUpdate();

                        // Retrieve the generated key (ID) if needed
                        int generatedId;
                        try (var rs = stmt.getGeneratedKeys()) {
                            if (rs.next()) {
                                generatedId = rs.getInt(1);
                            } else {
                                throw new SQLException("Creating task failed, no ID obtained.");
                            }
                        }

                        // Create response JSON object
                        JsonObject responseJson = new JsonObject();
                        responseJson.addProperty("message", "Task created successfully");

                        JsonObject dataJson = new JsonObject();
                        dataJson.addProperty("id", generatedId);
                        dataJson.addProperty("goalId", task.getGoalId());
                        dataJson.addProperty("title", task.getTitle());
                        dataJson.addProperty("description", task.getDescription());
                        dataJson.addProperty("status", task.getStatus());
                        dataJson.addProperty("dueDate", task.getDueDate());
                        responseJson.add("data", dataJson);

                        // Send JSON response
                        String response = new Gson().toJson(responseJson);
                        exchange.sendResponseHeaders(201, response.length()); // 201 Created
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(response.getBytes());
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    String response = "{\"message\": \"Database error: " + e.getMessage() + "\"}";
                    exchange.sendResponseHeaders(500, response.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                String response = "{\"message\": \"Server error: " + e.getMessage() + "\"}";
                exchange.sendResponseHeaders(500, response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }
        } else {
            exchange.sendResponseHeaders(405, -1); // Method Not Allowed
        }
    }
}

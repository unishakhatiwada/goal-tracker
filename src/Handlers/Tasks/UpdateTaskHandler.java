package Handlers.Tasks;

import Handlers.DatabaseConnection;
import Utils.URIHelper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateTaskHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("PUT".equals(exchange.getRequestMethod())) {
            int taskId = URIHelper.getId(exchange);
            TaskModel task = URIHelper.getRequestBody(exchange, TaskModel.class);

            try (Connection conn = DatabaseConnection.getConnection()) {
                String query = "UPDATE tasks SET title = ?, description = ?, status = ?, due_date = ? WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, task.getTitle());
                    stmt.setString(2, task.getDescription());
                    stmt.setString(3, task.getStatus());
                    stmt.setString(4, task.getDueDate());
                    stmt.setInt(5, taskId);

                    int rowsAffected = stmt.executeUpdate();

                    if (rowsAffected > 0) {
                        String response = "{\"message\": \"Task updated successfully\"}";
                        exchange.sendResponseHeaders(200, response.length());
                        try (var os = exchange.getResponseBody()) {
                            os.write(response.getBytes());
                        }
                    } else {
                        String response = "{\"message\": \"Task not found\"}";
                        exchange.sendResponseHeaders(404, response.length());
                        try (var os = exchange.getResponseBody()) {
                            os.write(response.getBytes());
                        }
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

package Handlers.Tasks;

import Handlers.DatabaseConnection;
import Utils.URIHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GetTaskHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            int taskId = URIHelper.getId(exchange);

            try (Connection conn = DatabaseConnection.getConnection()) {
                String query = "SELECT * FROM tasks WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setInt(1, taskId);
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        TaskModel task = new TaskModel();
                        task.setId(rs.getInt("id"));
                        task.setGoalId(rs.getInt("goal_id"));
                        task.setTitle(rs.getString("title"));
                        task.setDescription(rs.getString("description"));
                        task.setStatus(rs.getString("status"));
                        task.setDueDate(rs.getString("due_date"));
                        task.setCreatedAt(rs.getString("created_at"));
                        task.setUpdatedAt(rs.getString("updated_at"));

                        Gson gson = new GsonBuilder().create();
                        String response = gson.toJson(task);

                        exchange.sendResponseHeaders(200, response.length());
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(response.getBytes());
                        }
                    } else {
                        String response = "{\"message\": \"Task not found\"}";
                        exchange.sendResponseHeaders(404, response.length());
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(response.getBytes());
                        }
                    }
                }
            } catch (SQLException e) {
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

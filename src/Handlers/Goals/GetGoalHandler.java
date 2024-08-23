package Handlers.Goals;

import Handlers.DatabaseConnection;
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
import java.time.LocalDateTime;

public class GetGoalHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            String idParam = exchange.getRequestURI().getQuery().split("=")[1];
            int id = Integer.parseInt(idParam);

            try (Connection conn = DatabaseConnection.getConnection()) {
                String query = "SELECT * FROM goals WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setInt(1, id);
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        GoalModel goal = new GoalModel();
                        goal.setId(rs.getInt("id"));
                        goal.setTitle(rs.getString("title"));
                        goal.setPurpose(rs.getString("purpose"));
                        goal.setCompletion(rs.getBoolean("completion"));
                        goal.setDeadline(String.valueOf(rs.getObject("deadline", LocalDateTime.class)));
                        goal.setCreatedAt(String.valueOf(rs.getObject("created_at", LocalDateTime.class)));
                        goal.setUpdatedAt(String.valueOf(rs.getObject("updated_at", LocalDateTime.class)));

                        Gson gson = new GsonBuilder().create();
                        String response = gson.toJson(goal);

                        exchange.sendResponseHeaders(200, response.length());
                        OutputStream os = exchange.getResponseBody();
                        os.write(response.getBytes());
                        os.close();
                    } else {
                        String response = "{\"message\": \"Goal not found\"}";
                        exchange.sendResponseHeaders(404, response.length());
                        exchange.getResponseBody().write(response.getBytes());
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                String response = "{\"message\": \"Server error: " + e.getMessage() + "\"}";
                exchange.sendResponseHeaders(500, response.length());
                exchange.getResponseBody().write(response.getBytes());
            }
        }
    }
}

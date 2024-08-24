package Handlers.Tasks;

import Handlers.DatabaseConnection;
import Utils.URIHelper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeleteTaskHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("DELETE".equals(exchange.getRequestMethod())) {
            int taskId = URIHelper.getId(exchange);

            try (Connection conn = DatabaseConnection.getConnection()) {
                String query = "DELETE FROM tasks WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setInt(1, taskId);
                    int rowsAffected = stmt.executeUpdate();

                    String response = rowsAffected > 0 ? "{\"message\": \"Task deleted successfully\"}" :
                            "{\"message\": \"Task not found\"}";
                    exchange.sendResponseHeaders(rowsAffected > 0 ? 200 : 404, response.length());
                    exchange.getResponseBody().write(response.getBytes());
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

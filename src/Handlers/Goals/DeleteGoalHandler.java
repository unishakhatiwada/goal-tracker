package Handlers.Goals;

import Handlers.DatabaseConnection;
import Utils.URIHelper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeleteGoalHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {

        if ("DELETE".equals(exchange.getRequestMethod())) {

          int goalId = URIHelper.getId(exchange);
            try (Connection conn = DatabaseConnection.getConnection()) {
                String query = "DELETE FROM goals WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setInt(1, goalId);
                    int rowsAffected = stmt.executeUpdate();

                    if (rowsAffected > 0) {
                        sendResponse(exchange, 200, "{\"message\": \"Goal deleted successfully\"}");
                    } else {
                        sendResponse(exchange, 404, "{\"message\": \"Goal not found\"}");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                sendResponse(exchange, 500, "{\"message\": \"Server error: " + e.getMessage() + "\"}");
            } catch (Exception e){
                sendResponse(exchange, 500, "{\"message\": \"Server error:" + e.getMessage() + "\"}");
                exchange.getResponseBody().close();
            }
        } else {
            sendResponse(exchange, 405, "{\"message\": \"Method not allowed\"}");
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.length());
        exchange.getResponseBody().write(response.getBytes());
    }
}

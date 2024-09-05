package com.goaltracker.handlers.tasks;

import com.goaltracker.repositories.TaskRepository;
import com.goaltracker.utils.ResponseHelper;
import com.goaltracker.utils.URIHelper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.sql.SQLException;

public class ToggleTaskStatusHandler implements HttpHandler {

    private final TaskRepository taskRepository = new TaskRepository();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            try {
                int taskId = URIHelper.getId(exchange);
                Integer userId = (Integer) exchange.getAttribute("userId");

                if(!taskRepository.taskExists(taskId)){
                    ResponseHelper.sendErrorResponse(exchange, 404, "Task not found");
                    return;
                }

                if (userId == null) {
                    ResponseHelper.sendErrorResponse(exchange, 401, "Unauthorized");
                    return;
                }

                boolean isToggled = taskRepository.toggleTaskStatus(taskId, userId);

                if (isToggled) {
                    String newStatus = taskRepository.getTaskStatus(taskId, userId);
                    ResponseHelper.sendSuccessResponse(exchange, "Task status toggled successfully",
                            new StatusResponse(newStatus));
                } else {
                    ResponseHelper.sendErrorResponse(exchange, 404,
                            "Task not found or you don't have permission to modify it");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                ResponseHelper.sendErrorResponse(exchange, 500, "Server error: " + e.getMessage());
            }
        } else {
            exchange.sendResponseHeaders(405, -1);
        }
    }

    private static class StatusResponse {
        private final String status;

        public StatusResponse(String status) {
            this.status = status;
        }

        public String getStatus() {
            return status;
        }
    }
}
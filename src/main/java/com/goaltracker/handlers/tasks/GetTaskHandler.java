package com.goaltracker.handlers.tasks;

import com.goaltracker.models.Task;
import com.goaltracker.repositories.TaskRepository;
import com.goaltracker.utils.ResponseHelper;
import com.goaltracker.utils.URIHelper;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.sql.SQLException;

public class GetTaskHandler implements HttpHandler {

    private final TaskRepository taskRepository;

    public GetTaskHandler() {
        this.taskRepository = new TaskRepository();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            try {
                int taskId = URIHelper.getId(exchange);

                if (!taskRepository.taskExists(taskId)) {
                    ResponseHelper.sendErrorResponse(exchange, 404, "Task not found");
                    return;
                }

                Task task = taskRepository.getTaskDetail(taskId);
                ResponseHelper.sendSuccessResponse(exchange, "Task details retrieved successfully", task);
            } catch (NumberFormatException e) {
                ResponseHelper.sendErrorResponse(exchange, 400, "Invalid task ID");
            } catch (SQLException e) {
                ResponseHelper.sendErrorResponse(exchange, 500, "Database error: " + e.getMessage());
            } catch (Exception e) {
                ResponseHelper.sendErrorResponse(exchange, 500, "Server error: " + e.getMessage());
            }
        } else {
            exchange.sendResponseHeaders(405, -1); // Method Not Allowed
        }
    }
}

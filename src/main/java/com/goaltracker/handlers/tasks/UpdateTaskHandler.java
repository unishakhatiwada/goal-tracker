package com.goaltracker.handlers.tasks;

import com.goaltracker.utils.ResponseHelper;
import com.goaltracker.utils.URIHelper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.goaltracker.repositories.TaskRepository;
import com.goaltracker.models.Task;

import java.io.IOException;
import java.sql.SQLException;

public class UpdateTaskHandler implements HttpHandler {

    private final TaskRepository taskRepository = new TaskRepository();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("PUT".equals(exchange.getRequestMethod())) {
            int taskId = URIHelper.getId(exchange);
            Task task = URIHelper.getRequestBody(exchange, Task.class);


            try {
                if (!taskRepository.taskExists(taskId)) {
                    ResponseHelper.sendErrorResponse(exchange, 404, "Task not found");
                    return;
                }
                
                task.setId(taskId);

                if (taskRepository.updateTask(task)) {
                    ResponseHelper.sendSuccessResponse(exchange, "Task updated successfully",
                            Task.getTaskDetail(taskId));
                } else {
                    ResponseHelper.sendErrorResponse(
                            exchange,
                            404,
                            "Task not found");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                ResponseHelper.sendErrorResponse(
                        exchange,
                        500,
                        "Server error: " + e.getMessage());
            }
        } else {
            exchange.sendResponseHeaders(405, -1);
        }
    }
}
package Handlers.Tasks;

import Model.Task;
import Repositories.TaskRepository;
import Repositories.GoalRepository;
import Utils.URIHelper;
import Utils.ResponseHelper;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class CreateTaskHandler implements HttpHandler {

    private final TaskRepository taskRepository;
    private final GoalRepository goalRepository;

    public CreateTaskHandler() {
        this.taskRepository = new TaskRepository();
        this.goalRepository = new GoalRepository();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            try {
                Map<String, Object> requestBody = URIHelper.getRequestBody(exchange, Map.class);

                int goalId = ((Number) requestBody.get("goal_id")).intValue();
                List<Map<String, String>> taskMaps = (List<Map<String, String>>) requestBody.get("tasks");

                if (!goalRepository.goalExists(goalId)) {
                    ResponseHelper.sendErrorResponse(exchange, 404, "Goal not found");
                    return;
                }

                List<Task> tasks = taskMaps.stream()
                        .map(taskMap -> {
                            Task task = new Task();
                            task.setTitle(taskMap.get("title"));
                            return task;
                        })
                        .collect(java.util.stream.Collectors.toList());

                List<Task> createdTasks = taskRepository.createTasks(goalId, tasks);

                ResponseHelper.sendSuccessResponse(exchange, "Tasks created successfully", createdTasks);
            } catch (JsonSyntaxException e) {
                ResponseHelper.sendErrorResponse(exchange, 400, "Invalid JSON format.");
            } catch (SQLException e) {
                ResponseHelper.sendErrorResponse(exchange, 500, "Database error: " + e.getMessage());
            } catch (Exception e) {
                ResponseHelper.sendErrorResponse(exchange, 500, "Server error: " + e.getMessage());
            }
        } else {
            exchange.sendResponseHeaders(405, -1);
        }
    }
}
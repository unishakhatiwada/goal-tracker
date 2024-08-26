package Handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import Model.Goal;
import Repositories.GoalRepository;
import Repositories.TaskRepository;
import Utils.ResponseHelper;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.io.IOException;
import java.sql.SQLException;

public class DashboardHandler implements HttpHandler {
    private final GoalRepository goalRepository = new GoalRepository();
    private final TaskRepository taskRepository = new TaskRepository();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            try {
                Integer userId = (Integer) exchange.getAttribute("userId");
                if (userId == null) {
                    ResponseHelper.sendErrorResponse(exchange, 401, "Unauthorized");
                    return;
                }

                Map<String, Object> dashboardData = new HashMap<>();

                Map<String, Integer> goalCounts = goalRepository.getGoalCounts(userId);
                dashboardData.put("goalCounts", goalCounts);

                Map<String, Integer> taskCounts = taskRepository.getTaskCounts(userId);
                dashboardData.put("taskCounts", taskCounts);

                List<Goal> pendingGoals = goalRepository.getPendingGoals(userId, 10);
                dashboardData.put("pendingGoals", pendingGoals);

                ResponseHelper.sendSuccessResponse(exchange, "Dashboard data retrieved successfully", dashboardData);
            } catch (SQLException e) {
                e.printStackTrace();
                ResponseHelper.sendErrorResponse(exchange, 500, "Server error: " + e.getMessage());
            }
        } else {
            exchange.sendResponseHeaders(405, -1);
        }
    }
}

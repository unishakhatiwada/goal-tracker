package com.goaltracker.handlers.goals;

import com.goaltracker.models.Goal;
import com.goaltracker.utils.ResponseHelper;
import com.goaltracker.repositories.GoalRepository;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ListGoalHandler implements HttpHandler {

    private final GoalRepository goalRepository;

    public ListGoalHandler() {
        this.goalRepository = new GoalRepository();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            try {

                Integer userId = (Integer) exchange.getAttribute("userId");
                List<Goal> goals = goalRepository.getAllGoals(userId);

                ResponseHelper.sendSuccessResponse(exchange, "Goals retrieved successfully", goals);
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

package com.goaltracker.handlers.goals;

import com.goaltracker.utils.ResponseHelper;
import com.goaltracker.utils.URIHelper;
import com.goaltracker.repositories.GoalRepository;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.sql.SQLException;

public class DeleteGoalHandler implements HttpHandler {

    private final GoalRepository goalRepository = new GoalRepository();

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        if ("DELETE".equals(exchange.getRequestMethod())) {
            int goalId = URIHelper.getId(exchange);
            try {

                if (goalRepository.deleteGoal(goalId)) {
                    ResponseHelper.sendSuccessResponse(exchange, "Goal deleted successfully", null);
                } else {
                    ResponseHelper.sendErrorResponse(exchange, 404, "Goal not found");
                }
            } catch (SQLException e) {

                e.printStackTrace();
                ResponseHelper.sendErrorResponse(exchange, 500, "Server error: " + e.getMessage());
            }

        } else {
            ResponseHelper.sendErrorResponse(exchange, 405, "Method not allowed");

        }
    }
}

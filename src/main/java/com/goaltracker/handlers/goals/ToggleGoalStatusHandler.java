package com.goaltracker.handlers.goals;

import com.goaltracker.utils.ResponseHelper;
import com.goaltracker.utils.URIHelper;
import com.goaltracker.repositories.GoalRepository;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.sql.SQLException;

public class ToggleGoalStatusHandler implements HttpHandler {

    private final GoalRepository goalRepository = new GoalRepository();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            try {
                int goalId = URIHelper.getId(exchange);
                Integer userId = (Integer) exchange.getAttribute("userId");

                if (userId == null) {
                    ResponseHelper.sendErrorResponse(exchange, 401, "Unauthorized");
                    return;
                }

                if(!goalRepository.goalExists(goalId)){
                    ResponseHelper.sendErrorResponse(exchange, 404, "Goal not found");
                    return;
                }

                boolean isToggled = goalRepository.toggleGoalStatus(goalId, userId);

                if (isToggled) {
                    String newStatus = goalRepository.getGoalStatus(goalId, userId);
                    ResponseHelper.sendSuccessResponse(exchange, "Goal status toggled successfully",
                            new StatusResponse(newStatus));
                } else {
                    ResponseHelper.sendErrorResponse(exchange, 404,
                            "Goal not found or you don't have permission to modify it");
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
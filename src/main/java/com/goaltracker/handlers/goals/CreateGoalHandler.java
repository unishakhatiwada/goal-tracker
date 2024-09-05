package com.goaltracker.handlers.goals;

import com.goaltracker.models.Goal;
import com.goaltracker.utils.URIHelper;
import com.goaltracker.repositories.GoalRepository;
import com.goaltracker.utils.ResponseHelper;
import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.sql.SQLException;

public class CreateGoalHandler implements HttpHandler {

    private final GoalRepository goalRepository;

    public CreateGoalHandler() {
        this.goalRepository = new GoalRepository(); 
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            try {
                Goal goal = URIHelper.getRequestBody(exchange, Goal.class);
                Integer userId = (Integer) exchange.getAttribute("userId");

                int createdGoal = goalRepository.createGoal(goal, userId);

                ResponseHelper.sendSuccessResponse(exchange, "Goal created successfully", Goal.getGoalDetail(createdGoal));
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
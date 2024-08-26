package Handlers.Goals;

import Repositories.GoalRepository;
import Utils.URIHelper;
import Utils.ResponseHelper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import Model.Goal;

import java.io.IOException;
import java.util.Optional;

public class GetGoalHandler implements HttpHandler {

    private final GoalRepository goalRepository;

    public GetGoalHandler() {
        this.goalRepository = new GoalRepository();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            int goalId = URIHelper.getId(exchange);

            try {
                Optional<Goal> goalOptional = goalRepository.getGoalById(goalId);

                if (goalOptional.isPresent()) {
                    Goal goal = goalOptional.get();
                    ResponseHelper.sendSuccessResponse(exchange, "Goal retrieved successfully", goal);
                } else {
                    String response = "{\"message\": \"Goal not found\"}";
                    exchange.sendResponseHeaders(404, response.length());
                    exchange.getResponseBody().write(response.getBytes());
                }

            } catch (Exception e) {
                e.printStackTrace();
                String response = "{\"message\": \"Server error: " + e.getMessage() + "\"}";
                exchange.sendResponseHeaders(500, response.length());
                exchange.getResponseBody().write(response.getBytes());
            }
        } else {
            exchange.sendResponseHeaders(405, -1); // Method not allowed
        }
    }
}

package Handlers.Goals;

import Utils.ResponseHelper;
import Utils.URIHelper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import Repositories.GoalRepository;
import Model.Goal;

import java.io.IOException;
import java.sql.SQLException;

public class UpdateGoalHandler implements HttpHandler {

    private final GoalRepository goalRepository = new GoalRepository();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("PUT".equals(exchange.getRequestMethod())) {
            int goalId = URIHelper.getId(exchange);
            Goal goal = URIHelper.getRequestBody(exchange, Goal.class);

            try {
                goal.setId(goalId);
                boolean isUpdated = goalRepository.updateGoal(goal);

                if (isUpdated) {

                    ResponseHelper.sendSuccessResponse(exchange, "Goal updated successfully",
                            Goal.getGoalDetail(goalId));
                } else {
                    ResponseHelper.sendErrorResponse(
                            exchange,
                            404,
                            "Goal not found");
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

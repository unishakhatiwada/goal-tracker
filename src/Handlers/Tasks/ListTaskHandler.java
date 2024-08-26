package Handlers.Tasks;

import Model.Task;
import Repositories.TaskRepository;
import Utils.ResponseHelper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ListTaskHandler implements HttpHandler {

    private final TaskRepository taskRepository;

    public ListTaskHandler() {
        this.taskRepository = new TaskRepository();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            try {
                Integer userId = (Integer) exchange.getAttribute("userId");
                List<Task> tasks = taskRepository.getAllTasks(userId);

                ResponseHelper.sendSuccessResponse(exchange, "Tasks retrieved successfully", tasks);
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

package Handlers.Tasks;

import Utils.ResponseHelper;
import Utils.URIHelper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import Repositories.TaskRepository;

import java.io.IOException;
import java.sql.SQLException;

public class DeleteTaskHandler implements HttpHandler {

    private final TaskRepository taskRepository = new TaskRepository();

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        if ("DELETE".equals(exchange.getRequestMethod())) {

            int taskId = URIHelper.getId(exchange);
            try {

                if (!taskRepository.taskExists(taskId)) {
                    ResponseHelper.sendErrorResponse(exchange, 404, "Task not found");
                }

                if (taskRepository.deleteTask(taskId)) {
                    ResponseHelper.sendSuccessResponse(exchange, "Task deleted successfully", null);
                } else {
                    ResponseHelper.sendErrorResponse(exchange, 500, "Unable to delete task, PLease try again later");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                ResponseHelper.sendErrorResponse(exchange, 500, "Server error : " + e.getMessage());
            }
        } else {
            exchange.sendResponseHeaders(405, -1);
        }
    }
}

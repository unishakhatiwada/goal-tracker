package Api;

import Handlers.Goals.CreateGoalHandler;
import Handlers.Goals.DeleteGoalHandler;
import Handlers.Goals.GetGoalHandler;
import Handlers.Goals.UpdateGoalHandler;
import Handlers.LoginHandler;
import Handlers.LogoutHandler;
import Handlers.Tasks.CreateTaskHandler;
import Handlers.Tasks.DeleteTaskHandler;
import Handlers.Tasks.GetTaskHandler;
import Handlers.Tasks.UpdateTaskHandler;
import Middleware.TokenValidationFilter;
import Utils.Routes;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import Handlers.RegisterHandler;

public class ApiServer {
    public static void main(String[] args) {
        try {
            // Create an HTTP server listening on port 8000
            HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);

            // Define a context (endpoint) /register
            server.createContext("/register", new RegisterHandler());

            // Create context for the login handler WITHOUT token validation filter
            server.createContext("/login", new LoginHandler());

            // Register the LogoutHandler for the /logout endpoint with token validation
            var logoutContext = server.createContext("/logout", new LogoutHandler());
            logoutContext.getFilters().add(new TokenValidationFilter());

            // Adding context for goals with token validation
            var createGoalContext = server.createContext(Routes.CREATE_GOAL, new CreateGoalHandler());  // POST /goals/create/
            createGoalContext.getFilters().add(new TokenValidationFilter());

            var getGoalContext = server.createContext(Routes.LIST_GOAL, new GetGoalHandler()); // GET /goals/list/?id={id}
            getGoalContext.getFilters().add(new TokenValidationFilter());

            var updateGoalContext = server.createContext(Routes.UPDATE_GOAL, new UpdateGoalHandler()); // PUT /goals/update/?id={id}
            updateGoalContext.getFilters().add(new TokenValidationFilter());

            var deleteGoalContext = server.createContext(Routes.DELETE_GOAL, new DeleteGoalHandler()); // DELETE /goals/delete/?id={id}
            deleteGoalContext.getFilters().add(new TokenValidationFilter());

            // Adding context for tasks with token validation
            var createTaskContext = server.createContext(Routes.CREATE_TASK, new CreateTaskHandler());  // POST /tasks/create/
            createTaskContext.getFilters().add(new TokenValidationFilter());

            var getTaskContext = server.createContext(Routes.LIST_TASK, new GetTaskHandler());  // POST /tasks/list/?id={id}
            createTaskContext.getFilters().add(new TokenValidationFilter());

            var updateTaskContext = server.createContext(Routes.UPDATE_TASK, new UpdateTaskHandler());  // POST /tasks/update/?id={id}
            createTaskContext.getFilters().add(new TokenValidationFilter());

            var deleteTaskContext = server.createContext(Routes.DELETE_TASK, new DeleteTaskHandler());  // POST /tasks/delete/?id={id}
            createTaskContext.getFilters().add(new TokenValidationFilter());

            server.setExecutor(null); // creates a default executor

            // Start the server
            server.start();
            System.out.println("Server started on port " + server.getAddress().getPort());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

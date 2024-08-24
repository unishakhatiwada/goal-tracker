package Api;

import Handlers.Goals.CreateGoalHandler;
import Handlers.Goals.DeleteGoalHandler;
import Handlers.Goals.GetGoalHandler;
import Handlers.Goals.UpdateGoalHandler;
import Handlers.LoginHandler;
import Handlers.LogoutHandler;
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
            var createGoalContext = server.createContext(Routes.CREATE_GOAL, new CreateGoalHandler());  // POST /goals
            createGoalContext.getFilters().add(new TokenValidationFilter());

            var getGoalContext = server.createContext(Routes.LIST_GOAL, new GetGoalHandler()); // GET /goals?id={id}
            getGoalContext.getFilters().add(new TokenValidationFilter());

            var updateGoalContext = server.createContext(Routes.UPDATE_GOAL, new UpdateGoalHandler()); // PUT /goals?id={id}
            updateGoalContext.getFilters().add(new TokenValidationFilter());

            var deleteGoalContext = server.createContext(Routes.DELETE_GOAL, new DeleteGoalHandler()); // DELETE /goals?id={id}
            deleteGoalContext.getFilters().add(new TokenValidationFilter());

            server.setExecutor(null); // creates a default executor

            // Start the server
            server.start();
            System.out.println("Server started on port " + server.getAddress().getPort());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

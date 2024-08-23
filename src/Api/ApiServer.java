package Api;

import Handlers.LoginHandler;
import Handlers.LogoutHandler;
import Middleware.TokenValidationFilter;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import Handlers.RegisterHandler;

public class ApiServer {
    public static void main(String[] args) {
        try {
            // Create an HTTP server listening on port 8080
            HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
            // Define a context (endpoint) /register
            server.createContext("/register", new RegisterHandler());

            // Create context for the login handler and add token validation filter
            var loginContext = server.createContext("/login", new LoginHandler());
            loginContext.getFilters().add(new TokenValidationFilter());

            // Register the LogoutHandler for the /logout endpoint
            server.createContext("/logout", new LogoutHandler());
            server.setExecutor(null); // creates a default executor
            // Start the server
            server.start();
            System.out.println("Server started on port " + server.getAddress().getPort());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

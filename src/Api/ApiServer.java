package Api;

import Handlers.LoginHandler;
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
            // Define a context (endpoint) /login
            server.createContext("/login", new LoginHandler());
            // Set default executor
            server.setExecutor(null);
            // Start the server
            server.start();
            System.out.println("Server started on port " + server.getAddress().getPort());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

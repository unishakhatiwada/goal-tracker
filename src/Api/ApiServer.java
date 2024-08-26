package Api;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;

import Handlers.DashboardHandler;
import Handlers.LoginHandler;
import Handlers.LogoutHandler;
import Handlers.RegisterHandler;
import Handlers.Goals.CreateGoalHandler;
import Handlers.Goals.DeleteGoalHandler;
import Handlers.Goals.GetGoalHandler;
import Handlers.Goals.ListGoalHandler;
import Handlers.Goals.ToggleGoalStatusHandler;
import Handlers.Goals.UpdateGoalHandler;
import Handlers.Tasks.CreateTaskHandler;
import Handlers.Tasks.DeleteTaskHandler;
import Handlers.Tasks.GetTaskHandler;
import Handlers.Tasks.ListTaskHandler;
import Handlers.Tasks.ToggleTaskStatusHandler;
import Handlers.Tasks.UpdateTaskHandler;
import Utils.Routes;
import Middleware.TokenValidationFilter;

public class ApiServer {
    private static final int PORT = 9000;

    public static void main(String[] args) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
            setupContexts(server);
            server.setExecutor(null);
            server.start();
            System.out.println("Server started on port " + server.getAddress().getPort());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void setupContexts(HttpServer server) {
        server.createContext(Routes.REGISTER, new RegisterHandler());
        server.createContext(Routes.LOGIN, new LoginHandler());

        addContextWithTokenValidation(server, Routes.LOGOUT, new LogoutHandler());

        addContextWithTokenValidation(server, Routes.LIST_GOAL, new ListGoalHandler());
        addContextWithTokenValidation(server, Routes.GET_GOAL_DETAIL, new GetGoalHandler());
        addContextWithTokenValidation(server, Routes.CREATE_GOAL, new CreateGoalHandler());
        addContextWithTokenValidation(server, Routes.UPDATE_GOAL, new UpdateGoalHandler());
        addContextWithTokenValidation(server, Routes.DELETE_GOAL, new DeleteGoalHandler());
        addContextWithTokenValidation(server, Routes.TOGGLE_GOAL_STATUS, new ToggleGoalStatusHandler());

        addContextWithTokenValidation(server, Routes.LIST_TASK, new ListTaskHandler());
        addContextWithTokenValidation(server, Routes.CREATE_TASK, new CreateTaskHandler());
        addContextWithTokenValidation(server, Routes.GET_TASK_DETAIL, new GetTaskHandler());
        addContextWithTokenValidation(server, Routes.UPDATE_TASK, new UpdateTaskHandler());
        addContextWithTokenValidation(server, Routes.DELETE_TASK, new DeleteTaskHandler());
        addContextWithTokenValidation(server, Routes.TOGGLE_TASK_STATUS, new ToggleTaskStatusHandler());

        addContextWithTokenValidation(server,Routes.DASHBOARD, new DashboardHandler());
    }

    private static void addContextWithTokenValidation(HttpServer server, String path, HttpHandler handler) {
        var context = server.createContext(path, handler);
        context.getFilters().add(new TokenValidationFilter());
    }
}
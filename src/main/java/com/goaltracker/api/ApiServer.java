package com.goaltracker.api;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;

import com.goaltracker.utils.Routes;
import com.goaltracker.handlers.DashboardHandler;
import com.goaltracker.handlers.LoginHandler;
import com.goaltracker.handlers.LogoutHandler;
import com.goaltracker.handlers.RegisterHandler;
import com.goaltracker.handlers.goals.CreateGoalHandler;
import com.goaltracker.handlers.goals.DeleteGoalHandler;
import com.goaltracker.handlers.goals.GetGoalHandler;
import com.goaltracker.handlers.goals.ListGoalHandler;
import com.goaltracker.handlers.goals.ToggleGoalStatusHandler;
import com.goaltracker.handlers.goals.UpdateGoalHandler;
import com.goaltracker.handlers.tasks.CreateTaskHandler;
import com.goaltracker.handlers.tasks.DeleteTaskHandler;
import com.goaltracker.handlers.tasks.GetTaskHandler;
import com.goaltracker.handlers.tasks.ListTaskHandler;
import com.goaltracker.handlers.tasks.ToggleTaskStatusHandler;
import com.goaltracker.handlers.tasks.UpdateTaskHandler;
import com.goaltracker.middleware.TokenValidationFilter;

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

        addContextWithTokenValidation(server, Routes.DASHBOARD, new DashboardHandler());
    }

    private static void addContextWithTokenValidation(HttpServer server, String path, HttpHandler handler) {
        var context = server.createContext(path, handler);
        context.getFilters().add(new TokenValidationFilter());
    }
}

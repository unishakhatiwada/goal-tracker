package com.goaltracker.utils;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;

public class ResponseHelper {

    private static final Gson gson = new Gson();

    public static void sendResponse(HttpExchange exchange, int statusCode, String message, Object data)
            throws IOException {
        Response response = new Response(message, data);
        String jsonResponse = gson.toJson(response);

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, jsonResponse.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(jsonResponse.getBytes());
        }
    }

    public static void sendResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
        sendResponse(exchange, statusCode, message, null);
    }

    public static void sendSuccessResponse(HttpExchange exchange, String message, Object data) throws IOException {
        sendResponse(exchange, 200, message, data);
    }

    public static void sendErrorResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
        sendResponse(exchange, statusCode, message, null);
    }

    static class Response {
        private String message;
        private Object data;

        public Response(String message, Object data) {
            this.message = message;
            this.data = data;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }
    }
    
}

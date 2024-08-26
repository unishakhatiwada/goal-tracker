package Utils;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class URIHelper {
    public static int getId( HttpExchange exchange){
        //  Assuming the path is in the format /goals/{id}
        String[] pathSegments = exchange.getRequestURI().getQuery().split("=");

        return Integer.parseInt(pathSegments[1]);
    }
    public static <T> T getRequestBody( HttpExchange exchange,Class<T> model){
        //  Assuming the path is in the format /goals/{id}
        String requestBody = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))
                .lines().collect(Collectors.joining("\n"));

        System.out.println(requestBody);
        return new Gson().fromJson(requestBody, model);

    }
}

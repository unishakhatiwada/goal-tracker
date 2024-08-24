package Utils;

import com.sun.net.httpserver.HttpExchange;

public class URIHelper {
    public static int getId( HttpExchange exchange){
        //  Assuming the path is in the format /goals/{id}
        String[] pathSegments = exchange.getRequestURI().getQuery().split("=");

        return Integer.parseInt(pathSegments[1]);
    }
    public static int getQuery( HttpExchange exchange){
        //  Assuming the path is in the format /goals/{id}
        String[] pathSegments = exchange.getRequestURI().getQuery().split("=");

        return Integer.parseInt(pathSegments[1]);
    }
}

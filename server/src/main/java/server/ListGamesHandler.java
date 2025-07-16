package server;

import com.google.gson.Gson;
import service.*;
import spark.Request;
import spark.Response;

/**
 * An HTTP handler for the List Games service.
 */
public class ListGamesHandler {
    private ListGamesService listGamesService;

    public ListGamesHandler(ListGamesService listGamesService) {
        this.listGamesService = listGamesService;
    }

    /**
     * Calls listGames with HTTP/JSON input and output.
     * Used in Spark.get() in Server.java.
     *
     * @param request the HTTP request object
     * @param response the HTTP response object
     */
    public Object handleRequest(Request request, Response response) {
        Gson serializer = new Gson();
        String authToken = request.headers("authorization");
        ListGamesService.ListGamesRequest req = new ListGamesService.ListGamesRequest(authToken);
        try {
            ListGamesService.ListGamesResult res = listGamesService.listGames(req);
            response.status(200);
            return serializer.toJson(res);
        } catch (ServiceException e) {
            ErrorResult err = new ErrorResult(e.toString());
            response.status(e.getHTTPCode());
            return serializer.toJson(err);
        }
    }
}

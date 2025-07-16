package server;

import com.google.gson.Gson;
import service.*;
import spark.Request;
import spark.Response;

public class ListGamesHandler {
    private ListGamesService listGamesService;

    public ListGamesHandler(ListGamesService listGamesService) {
        this.listGamesService = listGamesService;
    }

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

package server;

import com.google.gson.Gson;
import service.*;
import spark.Request;
import spark.Response;

/**
 * An HTTP handler for the Create Game service.
 */
public class CreateGameHandler {
    private CreateGameService createGameService;

    public CreateGameHandler(CreateGameService createGameService) {
        this.createGameService = createGameService;
    }

    /**
     * Calls createGame with HTTP/JSON input and output.
     * Used in Spark.post() in Server.java.
     *
     * @param request the HTTP request object
     * @param response the HTTP response object
     */
    public Object handleRequest(Request request, Response response) {
        Gson gson = new Gson();
        CreateGameService.CreateGameRequest req = gson.fromJson(request.body(), CreateGameService.CreateGameRequest.class);
        String authToken = request.headers("authorization");
        req = new CreateGameService.CreateGameRequest(authToken, req.gameName());
        try {
            CreateGameService.CreateGameResult res = createGameService.createGame(req);
            response.status(200);
            return gson.toJson(res);
        } catch (ServiceException e) {
            ErrorResult err = new ErrorResult(e.toString());
            response.status(e.getHTTPCode());
            return gson.toJson(err);
        }
    }
}

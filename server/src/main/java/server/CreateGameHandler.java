package server;

import com.google.gson.Gson;
import service.*;
import spark.Request;
import spark.Response;

public class CreateGameHandler {
    private CreateGameService createGameService;

    public CreateGameHandler(CreateGameService createGameService) {
        this.createGameService = createGameService;
    }

    public Object handleRequest(Request request, Response response) {
        Gson serializer = new Gson();
        CreateGameService.CreateGameRequest req = serializer.fromJson(request.body(), CreateGameService.CreateGameRequest.class);
        String authToken = request.headers("authorization");
        req = new CreateGameService.CreateGameRequest(authToken, req.gameName());
        try {
            CreateGameService.CreateGameResult res = createGameService.createGame(req);
            response.status(200);
            return serializer.toJson(res);
        } catch (ServiceException e) {
            ErrorResult err = new ErrorResult(e.toString());
            response.status(e.getHTTPCode());
            return serializer.toJson(err);
        }
    }
}

package server;

import com.google.gson.Gson;
import service.*;
import spark.Request;
import spark.Response;

public class JoinGameHandler {
    private JoinGameService joinGameService;

    public JoinGameHandler(JoinGameService joinGameService) {
        this.joinGameService = joinGameService;
    }

    public Object handleRequest(Request request, Response response) {
        Gson serializer = new Gson();
        JoinGameService.JoinGameRequest req = serializer.fromJson(request.body(), JoinGameService.JoinGameRequest.class);
        String authToken = request.headers("authorization");
        req = new JoinGameService.JoinGameRequest(authToken, req.playerColor(), req.gameID());
        try {
            JoinGameService.JoinGameResult res = joinGameService.joinGame(req);
            response.status(200);
            return serializer.toJson(res);
        } catch (ServiceException e) {
            ErrorResult err = new ErrorResult(e.toString());
            response.status(e.getHTTPCode());
            return serializer.toJson(err);
        }
    }
}

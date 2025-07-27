package server;

import com.google.gson.Gson;
import model.*;
import service.*;
import spark.Request;
import spark.Response;

/**
 * An HTTP handler for the Join Game service.
 */
public class JoinGameHandler {
    public JoinGameHandler() {}

    /**
     * Calls joinGame with HTTP/JSON input and output.
     * Used in Spark.put() in Server.java.
     *
     * @param request the HTTP request object
     * @param response the HTTP response object
     */
    public Object handleRequest(Request request, Response response) {
        Gson gson = new Gson();
        JoinGameService service = new JoinGameService();
        JoinGameRequest req = gson.fromJson(request.body(), JoinGameRequest.class);
        String authToken = request.headers("authorization");
        req = new JoinGameRequest(authToken, req.playerColor(), req.gameID());
        try {
            JoinGameResult res = service.joinGame(req);
            response.status(200);
            return gson.toJson(res);
        } catch (ServiceException e) {
            ErrorResult err = new ErrorResult(e.toString());
            response.status(e.getHTTPCode());
            return gson.toJson(err);
        }
    }
}

package server;

import com.google.gson.Gson;
import model.*;
import service.*;
import spark.Request;
import spark.Response;

/**
 * An HTTP handler for the Create Game service.
 */
public class CreateGameHandler {
    public CreateGameHandler() {}

    /**
     * Calls createGame with HTTP/JSON input and output.
     * Used in Spark.post() in Server.java.
     *
     * @param request the HTTP request object
     * @param response the HTTP response object
     */
    public Object handleRequest(Request request, Response response) {
        Gson gson = new Gson();
        CreateGameService service = new CreateGameService();
        CreateGameRequest req = gson.fromJson(request.body(), CreateGameRequest.class);
        String authToken = request.headers("authorization");
        req = new CreateGameRequest(authToken, req.gameName());
        try {
            CreateGameResult res = service.createGame(req);
            response.status(200);
            return gson.toJson(res);
        } catch (ServiceException e) {
            ErrorResult err = new ErrorResult(e.getMessage());
            response.status(e.getHTTPCode());
            return gson.toJson(err);
        }
    }
}

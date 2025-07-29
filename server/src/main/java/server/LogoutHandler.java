package server;

import com.google.gson.Gson;
import model.*;
import model.request.LogoutRequest;
import model.result.LogoutResult;
import service.*;
import spark.Request;
import spark.Response;

/**
 * An HTTP handler for the Logout service.
 */
public class LogoutHandler {
    public LogoutHandler() {}

    /**
     * Calls logout with HTTP/JSON input and output.
     * Used in Spark.delete() in Server.java.
     *
     * @param request the HTTP request object
     * @param response the HTTP response object
     */
    public Object handleRequest(Request request, Response response) {
        Gson gson = new Gson();
        LogoutService service = new LogoutService();
        String authToken = request.headers("authorization");
        LogoutRequest req = new LogoutRequest(authToken);
        try {
            LogoutResult res = service.logout(req);
            response.status(200);
            return gson.toJson(res);
        } catch (ServiceException e) {
            ErrorResult err = new ErrorResult(e.getMessage());
            response.status(e.getHTTPCode());
            return gson.toJson(err);
        }
    }
}

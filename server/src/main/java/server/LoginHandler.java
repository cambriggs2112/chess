package server;

import com.google.gson.Gson;
import model.*;
import model.Request.LoginRequest;
import model.Result.LoginResult;
import service.*;
import spark.Request;
import spark.Response;

/**
 * An HTTP handler for the Login service.
 */
public class LoginHandler {
    public LoginHandler() {}

    /**
     * Calls login with HTTP/JSON input and output.
     * Used in Spark.post() in Server.java.
     *
     * @param request the HTTP request object
     * @param response the HTTP response object
     */
    public Object handleRequest(Request request, Response response) {
        Gson gson = new Gson();
        LoginService service = new LoginService();
        LoginRequest req = gson.fromJson(request.body(), LoginRequest.class);
        try {
            LoginResult res = service.login(req);
            response.status(200);
            return gson.toJson(res);
        } catch (ServiceException e) {
            ErrorResult err = new ErrorResult(e.getMessage());
            response.status(e.getHTTPCode());
            return gson.toJson(err);
        }
    }
}

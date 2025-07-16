package server;

import com.google.gson.Gson;
import service.*;
import spark.Request;
import spark.Response;

/**
 * An HTTP handler for the Login service.
 */
public class LoginHandler {
    private LoginService loginService;

    public LoginHandler(LoginService loginService) {
        this.loginService = loginService;
    }

    /**
     * Calls login with HTTP/JSON input and output.
     * Used in Spark.post() in Server.java.
     *
     * @param request the HTTP request object
     * @param response the HTTP response object
     */
    public Object handleRequest(Request request, Response response) {
        Gson gson = new Gson();
        LoginService.LoginRequest req = gson.fromJson(request.body(), LoginService.LoginRequest.class);
        try {
            LoginService.LoginResult res = loginService.login(req);
            response.status(200);
            return gson.toJson(res);
        } catch (ServiceException e) {
            ErrorResult err = new ErrorResult(e.toString());
            response.status(e.getHTTPCode());
            return gson.toJson(err);
        }
    }
}

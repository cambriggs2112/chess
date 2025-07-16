package server;

import com.google.gson.Gson;
import service.*;
import spark.Request;
import spark.Response;

public class LoginHandler {
    private LoginService loginService;

    public LoginHandler(LoginService loginService) {
        this.loginService = loginService;
    }

    public Object handleRequest(Request request, Response response) {
        Gson serializer = new Gson();
        LoginService.LoginRequest req = serializer.fromJson(request.body(), LoginService.LoginRequest.class);
        try {
            LoginService.LoginResult res = loginService.login(req);
            response.status(200);
            return serializer.toJson(res);
        } catch (ServiceException e) {
            ErrorResult err = new ErrorResult(e.toString());
            response.status(e.getHTTPCode());
            return serializer.toJson(err);
        }
    }
}

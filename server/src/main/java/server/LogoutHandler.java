package server;

import com.google.gson.Gson;
import service.*;
import spark.Request;
import spark.Response;

public class LogoutHandler {
    private LogoutService logoutService;

    public LogoutHandler(LogoutService logoutService) {
        this.logoutService = logoutService;
    }

    public Object handleRequest(Request request, Response response) {
        Gson serializer = new Gson();
        String authToken = request.headers("authorization");
        LogoutService.LogoutRequest req = new LogoutService.LogoutRequest(authToken);
        try {
            LogoutService.LogoutResult res = logoutService.logout(req);
            response.status(200);
            return serializer.toJson(res);
        } catch (ServiceException e) {
            ErrorResult err = new ErrorResult(e.toString());
            response.status(e.getHTTPCode());
            return serializer.toJson(err);
        }
    }
}

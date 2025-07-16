package server;

import com.google.gson.Gson;
import service.*;
import spark.Request;
import spark.Response;

public class RegisterHandler {
    private RegisterService registerService;

    public RegisterHandler(RegisterService registerService) {
        this.registerService = registerService;
    }

    public Object handleRequest(Request request, Response response) {
        Gson serializer = new Gson();
        RegisterService.RegisterRequest req = serializer.fromJson(request.body(), RegisterService.RegisterRequest.class);
        try {
            RegisterService.RegisterResult res = registerService.register(req);
            response.status(200);
            return serializer.toJson(res);
        } catch (ServiceException e) {
            ErrorResult err = new ErrorResult(e.toString());
            response.status(e.getHTTPCode());
            return serializer.toJson(err);
        }
    }
}

package server;

import com.google.gson.Gson;
import service.*;
import service.RegisterService.*;
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
        } catch (BadRequestException e) {
            ErrorResult err = new ErrorResult(e.toString());
            response.status(400);
            return serializer.toJson(err);
        } catch (ForbiddenException e) {
            ErrorResult err = new ErrorResult(e.toString());
            response.status(403);
            return serializer.toJson(err);
        } catch (Exception e) { // other exceptions
            ErrorResult err = new ErrorResult(e.toString());
            response.status(500);
            return serializer.toJson(err);
        }
    }
}

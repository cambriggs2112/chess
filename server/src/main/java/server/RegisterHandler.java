package server;

import com.google.gson.Gson;
import service.*;
import spark.Request;
import spark.Response;

/**
 * An HTTP handler for the Register service.
 */
public class RegisterHandler {
    public RegisterHandler() {}

    /**
     * Calls register with HTTP/JSON input and output.
     * Used in Spark.post() in Server.java.
     *
     * @param request the HTTP request object
     * @param response the HTTP response object
     */
    public Object handleRequest(Request request, Response response) {
        Gson gson = new Gson();
        RegisterService service = new RegisterService();
        RegisterService.RegisterRequest req = gson.fromJson(request.body(), RegisterService.RegisterRequest.class);
        try {
            RegisterService.RegisterResult res = service.register(req);
            response.status(200);
            return gson.toJson(res);
        } catch (ServiceException e) {
            ErrorResult err = new ErrorResult(e.toString());
            response.status(e.getHTTPCode());
            return gson.toJson(err);
        }
    }
}

package server;

import com.google.gson.Gson;
import model.*;
import model.Request.RegisterRequest;
import model.Result.RegisterResult;
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
        RegisterRequest req = gson.fromJson(request.body(), RegisterRequest.class);
        try {
            RegisterResult res = service.register(req);
            response.status(200);
            return gson.toJson(res);
        } catch (ServiceException e) {
            ErrorResult err = new ErrorResult(e.getMessage());
            response.status(e.getHTTPCode());
            return gson.toJson(err);
        }
    }
}

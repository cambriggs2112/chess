package server;

import com.google.gson.Gson;
import model.*;
import model.request.ClearApplicationRequest;
import model.result.ClearApplicationResult;
import service.*;
import spark.Request;
import spark.Response;

/**
 * An HTTP handler for the Clear Application service.
 */
public class ClearApplicationHandler {
    public ClearApplicationHandler() {}

    /**
     * Calls clearApplication with HTTP/JSON input and output.
     * Used in Spark.delete() in Server.java.
     *
     * @param request the HTTP request object
     * @param response the HTTP response object
     */
    public Object handleRequest(Request request, Response response) {
        Gson gson = new Gson();
        ClearApplicationService service = new ClearApplicationService();
        ClearApplicationRequest req = gson.fromJson(request.body(), ClearApplicationRequest.class);
        try {
            ClearApplicationResult res = service.clearApplication(req);
            response.status(200);
            return gson.toJson(res);
        } catch (ServiceException e) {
            ErrorResult err = new ErrorResult(e.getMessage());
            response.status(e.getHTTPCode());
            return gson.toJson(err);
        }
    }
}

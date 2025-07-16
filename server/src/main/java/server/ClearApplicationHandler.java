package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import service.*;
import spark.Request;
import spark.Response;

/**
 * An HTTP handler for the Clear Application service.
 */
public class ClearApplicationHandler {
    private ClearApplicationService clearApplicationService;

    public ClearApplicationHandler(ClearApplicationService clearApplicationService) {
        this.clearApplicationService = clearApplicationService;
    }

    /**
     * Calls clearApplication with HTTP/JSON input and output.
     * Used in Spark.delete() in Server.java.
     *
     * @param request the HTTP request object
     * @param response the HTTP response object
     */
    public Object handleRequest(Request request, Response response) {
        Gson gson = new Gson();
        ClearApplicationService.ClearApplicationRequest req = gson.fromJson(request.body(), ClearApplicationService.ClearApplicationRequest.class);
        try {
            ClearApplicationService.ClearApplicationResult res = clearApplicationService.clearApplication(req);
            response.status(200);
            return gson.toJson(res);
        } catch (ServiceException e) {
            ErrorResult err = new ErrorResult(e.toString());
            response.status(e.getHTTPCode());
            return gson.toJson(err);
        }
    }
}

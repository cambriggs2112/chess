package server;

import com.google.gson.Gson;
import service.*;
import spark.Request;
import spark.Response;

public class ClearApplicationHandler {
    private ClearApplicationService clearApplicationService;

    public ClearApplicationHandler(ClearApplicationService clearApplicationService) {
        this.clearApplicationService = clearApplicationService;
    }

    public Object handleRequest(Request request, Response response) {
        Gson serializer = new Gson();
        ClearApplicationService.ClearApplicationRequest req = serializer.fromJson(request.body(), ClearApplicationService.ClearApplicationRequest.class);
        try {
            ClearApplicationService.ClearApplicationResult res = clearApplicationService.clearApplication(req);
            response.status(200);
            return serializer.toJson(res);
        } catch (ServiceException e) {
            ErrorResult err = new ErrorResult(e.toString());
            response.status(e.getHTTPCode());
            return serializer.toJson(err);
        }
    }
}

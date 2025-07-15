package server;

import com.google.gson.Gson;
import service.*;
import service.ClearApplicationService.*;
import spark.Request;
import spark.Response;

public class ClearApplicationHandler {
    private ClearApplicationService clearApplicationService;

    public ClearApplicationHandler(ClearApplicationService clearApplicationService) {
        this.clearApplicationService = clearApplicationService;
    }

    public Object handleRequest(Request request, Response response) {
        Gson serializer = new Gson();
        ClearApplicationRequest req = serializer.fromJson(request.body(), ClearApplicationRequest.class);
        try {
            ClearApplicationResult res = clearApplicationService.clearApplication(req);
            response.status(200);
            return serializer.toJson(res);
        } catch (Exception e) {
            ErrorResult err = new ErrorResult(e.toString());
            response.status(500);
            return serializer.toJson(err);
        }
    }
}

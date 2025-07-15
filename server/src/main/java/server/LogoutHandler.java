package server;

import com.google.gson.Gson;
import service.LogoutService;
import service.LogoutService.*;

public class LogoutHandler {
    private LogoutService logoutService;

    public LogoutHandler(LogoutService logoutService) {
        this.logoutService = logoutService;
    }
}

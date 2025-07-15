package server;

import com.google.gson.Gson;
import service.LoginService;
import service.LoginService.*;

public class LoginHandler {
    private LoginService loginService;

    public LoginHandler(LoginService loginService) {
        this.loginService = loginService;
    }
}

package service;

import dataaccess.*;

public class LoginService {
    public record LoginRequest(String username, String password) {}
    public record LoginResult(String username, String authToken) {}

    private AuthDAO auth;
    private GameDAO game;
    private UserDAO user;

    public LoginService(AuthDAO auth, GameDAO game, UserDAO user) {
        this.auth = auth;
        this.game = game;
        this.user = user;
    }

    public LoginResult login(LoginRequest request) {
        return null;
    }
}

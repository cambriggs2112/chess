package service;

import dataaccess.*;

public class LogoutService {
    public record LogoutRequest(String authToken) {}
    public record LogoutResult() {}

    private AuthDAO auth;
    private GameDAO game;
    private UserDAO user;

    public LogoutService(AuthDAO auth, GameDAO game, UserDAO user) {
        this.auth = auth;
        this.game = game;
        this.user = user;
    }

    public LogoutResult logout(LogoutRequest request) {
        return null;
    }
}

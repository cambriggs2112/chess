package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

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

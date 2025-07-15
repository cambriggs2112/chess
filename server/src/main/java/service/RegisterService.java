package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

public class RegisterService {
    public record RegisterRequest(String username, String password, String email) {}

    public record RegisterResult(String username, String authToken) {}

    private AuthDAO auth;
    private GameDAO game;
    private UserDAO user;

    public RegisterService(AuthDAO auth, GameDAO game, UserDAO user) {
        this.auth = auth;
        this.game = game;
        this.user = user;
    }

    public RegisterResult register(RegisterRequest request) {
        return null;
    }
}

package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

public class CreateGameService {
    public record CreateGameRequest(String authToken, String gameName) {}

    public record CreateGameResult(int gameID) {}

    private AuthDAO auth;
    private GameDAO game;
    private UserDAO user;

    public CreateGameService(AuthDAO auth, GameDAO game, UserDAO user) {
        this.auth = auth;
        this.game = game;
        this.user = user;
    }

    public CreateGameResult createGame(CreateGameRequest request) {
        return null;
    }
}

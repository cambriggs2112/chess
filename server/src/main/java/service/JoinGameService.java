package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

public class JoinGameService {
    public record JoinGameRequest(String authToken, ChessGame.TeamColor playerColor, int gameID) {}

    public record JoinGameResult() {}

    private AuthDAO auth;
    private GameDAO game;
    private UserDAO user;

    public JoinGameService(AuthDAO auth, GameDAO game, UserDAO user) {
        this.auth = auth;
        this.game = game;
        this.user = user;
    }

    public JoinGameResult joinGame(JoinGameRequest request) {
        return null;
    }
}

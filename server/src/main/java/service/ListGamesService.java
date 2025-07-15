package service;

import java.util.ArrayList;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;

public class ListGamesService {
    public record ListGamesRequest(String authToken) {}
    public record ListGamesResult(ArrayList<GameData> games) {}

    private AuthDAO auth;
    private GameDAO game;
    private UserDAO user;

    public ListGamesService(AuthDAO auth, GameDAO game, UserDAO user) {
        this.auth = auth;
        this.game = game;
        this.user = user;
    }

    public ListGamesResult listGames(ListGamesRequest request) throws UnauthorizedException, InternalServerErrorException {
        ArrayList<GameData> result;
        try {
            if (auth.getAuth(request.authToken()) == null) {
                throw new UnauthorizedException("[401] Unauthorized: Unknown authorization token provided whilst attempting to list games.");
            }
            result = game.listGames();
        } catch (DataAccessException e) {
            throw new InternalServerErrorException("[500] Internal Server Error occurred whilst attempting to list games: " + e);
        }
        return new ListGamesResult(result);
    }
}

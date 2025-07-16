package service;

import java.util.ArrayList;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;

public class ListGamesService {
    public record ListGamesRequest(String authToken) {}
    public record ListGamesResult(ArrayList<ListGamesResultElement> games) {}
    public record ListGamesResultElement(int gameID, String whiteUsername, String blackUsername, String gameName) {}

    private AuthDAO auth;
    private GameDAO game;
    private UserDAO user;

    public ListGamesService(AuthDAO auth, GameDAO game, UserDAO user) {
        this.auth = auth;
        this.game = game;
        this.user = user;
    }

    public ListGamesResult listGames(ListGamesRequest request) throws ServiceException {
        ArrayList<GameData> gameList;
        ArrayList<ListGamesResultElement> result = new ArrayList<ListGamesResultElement>();
        try {
            if (request.authToken() == null || auth.getAuth(request.authToken()) == null) {
                throw new ServiceException("Unauthorized: Unknown authorization token provided whilst attempting to list games.", 401);
            }
            gameList = game.listGames();
        } catch (DataAccessException e) {
            throw new ServiceException("Internal Server Error occurred whilst attempting to list games: " + e, 500);
        }
        for (GameData thisGame : gameList) {
            result.add(new ListGamesResultElement(thisGame.gameID(), thisGame.whiteUsername(), thisGame.blackUsername(), thisGame.gameName()));
        }
        return new ListGamesResult(result);
    }
}

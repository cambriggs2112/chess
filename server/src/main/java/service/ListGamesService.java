package service;

import java.util.ArrayList;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;

/**
 * A service that lists games to authenticated users.
 * Each listed entry contains a game ID, the usernames of both players (if applicable), and the
 * game name.
 */
public class ListGamesService {
    public record ListGamesRequest(String authToken) {}
    public record ListGamesResult(ArrayList<ListGamesResultElement> games) {}
    public record ListGamesResultElement(Integer gameID, String whiteUsername, String blackUsername, String gameName) {}

    private AuthDAO auth;
    private GameDAO game;
    private UserDAO user;

    public ListGamesService(AuthDAO auth, GameDAO game, UserDAO user) {
        this.auth = auth;
        this.game = game;
        this.user = user;
    }

    /**
     * Returns an ArrayList of games to a user.
     *
     * @param request the request object (authToken)
     * @return a result object (ArrayList of objects containing game IDs, white usernames,
     *                black usernames, and game names)
     * @throws ServiceException if authorization token is incorrect (401) or error occurs with
     *                data access (500)
     */
    public ListGamesResult listGames(ListGamesRequest request) throws ServiceException {
        ArrayList<GameData> gameList;
        ArrayList<ListGamesResultElement> result = new ArrayList<ListGamesResultElement>();
        try {
            if (request.authToken() == null || auth.getAuth(request.authToken()) == null) {
                throw new ServiceException("ERROR: Unauthorized: Unknown authorization token provided whilst attempting to list games.", 401);
            }
            gameList = game.listGames();
        } catch (DataAccessException e) {
            throw new ServiceException("ERROR: Internal Server Error occurred whilst attempting to list games: " + e, 500);
        }
        for (GameData thisGame : gameList) {
            result.add(new ListGamesResultElement(thisGame.gameID(), thisGame.whiteUsername(), thisGame.blackUsername(), thisGame.gameName()));
        }
        return new ListGamesResult(result);
    }
}

package service;

import java.util.ArrayList;

import dataaccess.*;
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

    public ListGamesService() {}

    /**
     * Returns an ArrayList of games to a user.
     */
    public ListGamesResult listGames(ListGamesRequest request) throws ServiceException {
        ArrayList<GameData> gameList;
        ArrayList<ListGamesResultElement> result = new ArrayList<ListGamesResultElement>();
        try {
            MemoryAuthDAO auth = new MemoryAuthDAO();
            MemoryGameDAO game = new MemoryGameDAO();
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

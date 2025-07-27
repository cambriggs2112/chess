package service;

import chess.ChessGame;
import dataaccess.*;
import model.*;
import java.util.UUID;

/**
 * A service that create games.
 * A name is provided by an authenticated user to create a game with a unique game ID.
 */
public class CreateGameService {

    public CreateGameService() {}

    /**
     * Adds a new game to the database by obtaining a game name from the user and generating a
     * unique game ID. A new ChessGame object is created and null username fields are generated.
     * A game ID is returned to the user upon successful game creation.
     *
     * @param request the request object (authToken, gameName)
     * @return a result object (gameID)
     * @throws ServiceException if required fields are missing (400), authorization token is
     *                incorrect (401), or error occurs with data access (500)
     */
    public CreateGameResult createGame(CreateGameRequest request) throws ServiceException {
        Integer gameID = null;
        if (request.gameName() == null || request.gameName().isEmpty()) {
            throw new ServiceException("ERROR: Bad Request: Game name is required to create game.", 400);
        }
        try {
            SQLAuthDAO auth = new SQLAuthDAO();
            SQLGameDAO game = new SQLGameDAO();
            if (request.authToken() == null || auth.getAuth(request.authToken()) == null) {
                throw new ServiceException("ERROR: Unauthorized: Unknown authorization token provided whilst attempting to create game.", 401);
            }
            while (gameID == null || gameID == 0 || game.getGame(gameID) != null) { // Used to effectively guarantee gameID is valid and unique
                gameID = Math.abs(UUID.randomUUID().hashCode()); // Get gameID by hashing UUID and applying absolute value
            }
            GameData newGame = new GameData(gameID, null, null, request.gameName(), new ChessGame());
            game.createGame(newGame);
        } catch (DataAccessException e) {
            throw new ServiceException("ERROR: Internal Server Error occurred whilst attempting to create game: " + e, 500);
        }
        return new CreateGameResult(gameID);
    }
}

package service;

import chess.ChessGame;
import dataaccess.*;
import model.GameData;
import java.util.UUID;

public class CreateGameService {
    public record CreateGameRequest(String authToken, String gameName) {}
    public record CreateGameResult(Integer gameID) {}

    private AuthDAO auth;
    private GameDAO game;
    private UserDAO user;

    public CreateGameService(AuthDAO auth, GameDAO game, UserDAO user) {
        this.auth = auth;
        this.game = game;
        this.user = user;
    }

    public CreateGameResult createGame(CreateGameRequest request) throws ServiceException {
        Integer gameID = null;
        if (request.gameName() == null || request.gameName().isEmpty()) {
            throw new ServiceException("ERROR: Bad Request: Game name is required to create game.", 400);
        }
        try {
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

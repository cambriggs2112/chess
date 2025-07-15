package service;

import chess.ChessGame;
import dataaccess.*;
import model.GameData;
import java.util.UUID;

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

    public CreateGameResult createGame(CreateGameRequest request) throws BadRequestException, UnauthorizedException, InternalServerErrorException {
        int gameID = 0;
        if (request.gameName().isEmpty()) {
            throw new BadRequestException("[400] Bad Request: Game name is required to create game.");
        }
        try {
            if (auth.getAuth(request.authToken()) == null) {
                throw new UnauthorizedException("[401] Unauthorized: Unknown authorization token provided whilst attempting to create game.");
            }
            gameID = UUID.randomUUID().hashCode(); // Get pseudorandom, likely unique int by hashing UUID
            GameData newGame = new GameData(gameID, "", "", request.gameName(), new ChessGame());
            game.createGame(newGame);
        } catch (DataAccessException e) {
            throw new InternalServerErrorException("[500] Internal Server Error occurred whilst attempting to create game: " + e);
        }
        return new CreateGameResult(gameID);
    }
}

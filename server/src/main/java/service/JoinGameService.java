package service;

import chess.ChessGame;
import dataaccess.*;
import model.*;

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

    public JoinGameResult joinGame(JoinGameRequest request) throws BadRequestException, UnauthorizedException, ForbiddenException, InternalServerErrorException {
        if (request.authToken() == null) {
            throw new UnauthorizedException("[401] Unauthorized: Unknown authorization token provided whilst attempting to join game.");
        }
        if (request.playerColor() == null) {
            throw new BadRequestException("[400] Bad Request: Team color is required and must be BLACK or WHITE to join game.");
        }
        if (request.gameID() < 0) {
            throw new UnauthorizedException("[400] Bad Request: Invalid game ID provided whilst attempting to join game.");
        }
        try {
            AuthData thisAuth = auth.getAuth(request.authToken());
            if (thisAuth == null) {
                throw new UnauthorizedException("[401] Unauthorized: Unknown authorization token provided whilst attempting to join game.");
            }
            GameData thisGame = game.getGame(request.gameID());
            GameData newGame;
            if (thisGame == null) {
                throw new ForbiddenException("[403] Forbidden: Unknown game ID provided whilst attempting to join game.");
            }
            if (request.playerColor() == ChessGame.TeamColor.WHITE && thisGame.whiteUsername().isEmpty()) {
                newGame = new GameData(request.gameID(), thisAuth.username(), thisGame.blackUsername(), thisGame.gameName(), thisGame.game());
            } else if (request.playerColor() == ChessGame.TeamColor.BLACK && thisGame.blackUsername().isEmpty()) {
                newGame = new GameData(request.gameID(), thisGame.whiteUsername(), thisAuth.username(), thisGame.gameName(), thisGame.game());
            } else {
                throw new ForbiddenException("[403] Forbidden: Unable to join game since the provided team is already taken.");
            }
            game.updateGame(newGame);
        } catch (DataAccessException e) {
            throw new InternalServerErrorException("[500] Internal Server Error occurred whilst attempting to join game: " + e);
        }
        return new JoinGameResult();
    }
}

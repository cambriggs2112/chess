package service;

import chess.ChessGame;
import dataaccess.*;
import model.*;

/**
 * A service that allows users to join games.
 * An authenticated user chooses a player color (BLACK or WHITE) and chooses a game by game ID to join.
 * Users are not allowed to join occupied teams.
 */
public class JoinGameService {
    public record JoinGameRequest(String authToken, ChessGame.TeamColor playerColor, Integer gameID) {}
    public record JoinGameResult() {}

    private AuthDAO auth;
    private GameDAO game;
    private UserDAO user;

    public JoinGameService(AuthDAO auth, GameDAO game, UserDAO user) {
        this.auth = auth;
        this.game = game;
        this.user = user;
    }

    /**
     * Adds a user to an existing game by game ID.
     * The user must provide a team color to be added to (WHITE or BLACK).
     *
     * @param request the request object (authToken, playerColor, gameID)
     * @return a result object
     * @throws ServiceException if required fields are missing or invalid (400), authorization
     *                token is incorrect (401), game ID is unknown (403), provided team color
     *                is already taken (403), or error occurs with data access (500)
     */
    public JoinGameResult joinGame(JoinGameRequest request) throws ServiceException {
        if (request.authToken() == null) {
            throw new ServiceException("ERROR: Unauthorized: Unknown authorization token provided whilst attempting to join game.", 401);
        }
        if (request.playerColor() == null) {
            throw new ServiceException("ERROR: Bad Request: Team color is required and must be BLACK or WHITE to join game.", 400);
        }
        if (request.gameID() == null) {
            throw new ServiceException("ERROR: Bad Request: Game ID is required to join game.", 400);
        }
        if (request.gameID() <= 0) {
            throw new ServiceException("ERROR: Bad Request: Invalid game ID provided whilst attempting to join game.", 400);
        }
        try {
            AuthData thisAuth = auth.getAuth(request.authToken());
            if (thisAuth == null) {
                throw new ServiceException("ERROR: Unauthorized: Unknown authorization token provided whilst attempting to join game.", 401);
            }
            GameData thisGame = game.getGame(request.gameID());
            GameData newGame;
            if (thisGame == null) {
                throw new ServiceException("ERROR: Forbidden: Unknown game ID provided whilst attempting to join game.", 403);
            }
            if (request.playerColor() == ChessGame.TeamColor.WHITE && thisGame.whiteUsername() == null) {
                newGame = new GameData(request.gameID(), thisAuth.username(), thisGame.blackUsername(), thisGame.gameName(), thisGame.game());
            } else if (request.playerColor() == ChessGame.TeamColor.BLACK && thisGame.blackUsername() == null) {
                newGame = new GameData(request.gameID(), thisGame.whiteUsername(), thisAuth.username(), thisGame.gameName(), thisGame.game());
            } else {
                throw new ServiceException("ERROR: Forbidden: Unable to join game since the provided team is already taken.", 403);
            }
            game.updateGame(newGame);
        } catch (DataAccessException e) {
            throw new ServiceException("ERROR: Internal Server Error occurred whilst attempting to join game: " + e, 500);
        }
        return new JoinGameResult();
    }
}

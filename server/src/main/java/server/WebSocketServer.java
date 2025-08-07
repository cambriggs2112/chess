package server;

import chess.*;
import com.google.gson.Gson;
import dataaccess.*;
import model.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.*;
import websocket.commands.*;
import websocket.messages.*;
import java.io.*;

@WebSocket
public class WebSocketServer {
    private ConnectionManager connectionManager;

    public WebSocketServer() {
        this.connectionManager = new ConnectionManager();
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        Gson gson = new Gson();
        UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
        String authToken = command.getAuthToken();
        int gameID = command.getGameID();
        try {
            SQLAuthDAO auths = new SQLAuthDAO();
            SQLGameDAO games = new SQLGameDAO();
            connectionManager.addConnection(authToken, gameID, session);
            if (auths.getAuth(authToken) == null) {
                connectionManager.broadcastOne(authToken, new ErrorMessage("ERROR: Auth token is unknown."));
                return;
            }
            if (games.getGame(gameID) == null) {
                connectionManager.broadcastOne(authToken, new ErrorMessage("ERROR: Game ID is unknown."));
                return;
            }
            AuthData thisAuth = auths.getAuth(authToken);
            GameData thisGame = games.getGame(gameID);
            ChessGame.TeamColor userColor = null;
            if (thisAuth.username().equals(thisGame.whiteUsername())) {
                userColor = ChessGame.TeamColor.WHITE;
            }
            if (thisAuth.username().equals(thisGame.blackUsername())) {
                userColor = ChessGame.TeamColor.BLACK;
            }
            switch (command.getCommandType()) {
                case CONNECT:
                    onConnect(thisAuth, thisGame, userColor);
                    break;
                case MAKE_MOVE:
                    onMakeMove(thisAuth, thisGame, userColor, games, gson.fromJson(message, MakeMoveCommand.class));
                    break;
                case LEAVE:
                    onLeave(thisAuth, thisGame, userColor, games);
                    break;
                case RESIGN:
                    onResign(thisAuth, thisGame, userColor, games);
                    break;
            }
        } catch (DataAccessException e) {
            connectionManager.broadcastOne(authToken, new ErrorMessage("ERROR: " + e.getMessage()));
        }
    }

    private void onConnect(AuthData thisAuth, GameData thisGame, ChessGame.TeamColor userColor) {
        connectionManager.broadcastOne(thisAuth.authToken(), new LoadGameMessage(thisGame.game(), null));
        if (userColor == null) {
            connectionManager.broadcastAllExcept(thisAuth.authToken(), thisGame.gameID(), new NotificationMessage(
                    thisAuth.username() + " joined as an observer."));
        } else {
            connectionManager.broadcastAllExcept(thisAuth.authToken(), thisGame.gameID(), new NotificationMessage(
                    thisAuth.username() + " joined as the " + userColor + " player."));
        }
    }

    private void onMakeMove(AuthData thisAuth, GameData thisGame, ChessGame.TeamColor userColor, GameDAO games,
                            MakeMoveCommand moveCommand) throws DataAccessException {
        ChessMove move = moveCommand.getMove();
        ChessGame updatedGame = thisGame.game();
        if (userColor == null) {
            connectionManager.broadcastOne(thisAuth.authToken(), new ErrorMessage("ERROR: Observers cannot move pieces."));
            return;
        }
        if (!updatedGame.getGameActive()) {
            connectionManager.broadcastOne(thisAuth.authToken(), new ErrorMessage("ERROR: The game is over."));
            return;
        }
        if (userColor != updatedGame.getTeamTurn()) {
            connectionManager.broadcastOne(thisAuth.authToken(), new ErrorMessage("ERROR: It is not your turn."));
            return;
        }
        try {
            String extraNotification = null;
            updatedGame.makeMove(move);
            if (updatedGame.isInCheckmate(ChessGame.TeamColor.WHITE)) {
                extraNotification = "WHITE team (" + thisGame.whiteUsername() + ") is in checkmate!";
            } else if (updatedGame.isInCheck(ChessGame.TeamColor.WHITE)) {
                extraNotification = "WHITE team (" + thisGame.whiteUsername() + ") is in check!";
            } else if (updatedGame.isInStalemate(ChessGame.TeamColor.WHITE)) {
                extraNotification = "WHITE team (" + thisGame.whiteUsername() + ") is in stalemate!";
            }
            if (updatedGame.isInCheckmate(ChessGame.TeamColor.BLACK)) {
                extraNotification = "BLACK team (" + thisGame.blackUsername() + ") is in checkmate!";
            } else if (updatedGame.isInCheck(ChessGame.TeamColor.BLACK)) {
                extraNotification = "BLACK team (" + thisGame.blackUsername() + ") is in check!";
            } else if (updatedGame.isInStalemate(ChessGame.TeamColor.BLACK)) {
                extraNotification = "BLACK team (" + thisGame.blackUsername() + ") is in stalemate!";
            }
            games.updateGame(new GameData(thisGame.gameID(), thisGame.whiteUsername(), thisGame.blackUsername(), thisGame.gameName(), updatedGame));
            connectionManager.broadcastAllExcept(null, thisGame.gameID(), new LoadGameMessage(updatedGame, move));
            connectionManager.broadcastAllExcept(thisAuth.authToken(), thisGame.gameID(), new NotificationMessage(
                    thisAuth.username() + " made move " + move + "."));
            if (extraNotification != null) {
                connectionManager.broadcastAllExcept(null, thisGame.gameID(), new NotificationMessage(extraNotification));
            }
        } catch (InvalidMoveException e) {
            connectionManager.broadcastOne(thisAuth.authToken(), new ErrorMessage("ERROR: " + e.getMessage()));
        }
    }

    private void onLeave(AuthData thisAuth, GameData thisGame, ChessGame.TeamColor userColor, GameDAO games) throws DataAccessException {
        connectionManager.removeConnection(thisAuth.authToken());
        connectionManager.broadcastAllExcept(thisAuth.authToken(), thisGame.gameID(), new NotificationMessage(
                thisAuth.username() + " left the game."));
        if (userColor == ChessGame.TeamColor.WHITE) {
            games.updateGame(new GameData(thisGame.gameID(), null, thisGame.blackUsername(), thisGame.gameName(), thisGame.game()));
        } else {
            games.updateGame(new GameData(thisGame.gameID(), thisGame.whiteUsername(), null, thisGame.gameName(), thisGame.game()));
        }
    }

    private void onResign(AuthData thisAuth, GameData thisGame, ChessGame.TeamColor userColor, GameDAO games) throws DataAccessException {
        ChessGame updatedGame = thisGame.game();
        if (userColor == null) {
            connectionManager.broadcastOne(thisAuth.authToken(), new ErrorMessage("ERROR: Observers cannot resign."));
            return;
        }
        if (!updatedGame.getGameActive()) {
            connectionManager.broadcastOne(thisAuth.authToken(), new ErrorMessage("ERROR: The game is over."));
            return;
        }
        updatedGame.endGame();
        games.updateGame(new GameData(thisGame.gameID(), thisGame.whiteUsername(), thisGame.blackUsername(), thisGame.gameName(), updatedGame));
        connectionManager.broadcastAllExcept(null, thisGame.gameID(), new NotificationMessage(
                userColor + " team (" + thisAuth.username() + ") resigned!"));
    }
}

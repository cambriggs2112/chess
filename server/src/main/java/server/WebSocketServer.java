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
//            System.out.println("BEGIN");
//            connectionManager.printConnections();
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
            ChessGame updatedGame = thisGame.game();
            ChessGame.TeamColor userColor = null;
            if (thisAuth.username().equals(thisGame.whiteUsername())) {
                userColor = ChessGame.TeamColor.WHITE;
            }
            if (thisAuth.username().equals(thisGame.blackUsername())) {
                userColor = ChessGame.TeamColor.BLACK;
            }
            switch (command.getCommandType()) {
                case CONNECT:
                    connectionManager.broadcastOne(authToken, new LoadGameMessage(thisGame.game(), null));
                    if (userColor == null) {
                        connectionManager.broadcastAllExcept(authToken, gameID, new NotificationMessage(thisAuth.username() + " joined as an observer."));
                    } else {
                        connectionManager.broadcastAllExcept(authToken, gameID, new NotificationMessage(thisAuth.username() + " joined as the " + userColor + " player."));
                    }
                    break;
                case MAKE_MOVE:
                    MakeMoveCommand moveCommand = gson.fromJson(message, MakeMoveCommand.class);
                    ChessMove move = moveCommand.getMove();
                    if (userColor == null) {
                        connectionManager.broadcastOne(authToken, new ErrorMessage("ERROR: Observers cannot move pieces."));
                        break;
                    }
                    if (!updatedGame.getGameActive()) {
                        connectionManager.broadcastOne(authToken, new ErrorMessage("ERROR: The game is over."));
                        break;
                    }
                    if (userColor != updatedGame.getTeamTurn()) {
                        connectionManager.broadcastOne(authToken, new ErrorMessage("ERROR: It is not your turn."));
                        break;
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
                        games.updateGame(new GameData(gameID, thisGame.whiteUsername(), thisGame.blackUsername(), thisGame.gameName(), updatedGame));
                        connectionManager.broadcastAllExcept(null, gameID, new LoadGameMessage(updatedGame, move));
                        connectionManager.broadcastAllExcept(authToken, gameID, new NotificationMessage(thisAuth.username() + " made move " + move + "."));
                        if (extraNotification != null) {
                            connectionManager.broadcastAllExcept(null, gameID, new NotificationMessage(extraNotification));
                        }
                    } catch (InvalidMoveException e) {
                        connectionManager.broadcastOne(authToken, new ErrorMessage("ERROR: " + e.getMessage()));
                    }
                    break;
                case LEAVE:
                    connectionManager.removeConnection(authToken);
                    connectionManager.broadcastAllExcept(authToken, gameID, new NotificationMessage(thisAuth.username() + " left the game."));
                    if (userColor == ChessGame.TeamColor.WHITE) {
                        games.updateGame(new GameData(gameID, null, thisGame.blackUsername(), thisGame.gameName(), thisGame.game()));
                    } else {
                        games.updateGame(new GameData(gameID, thisGame.whiteUsername(), null, thisGame.gameName(), thisGame.game()));
                    }
                    break;
                case RESIGN:
                    if (userColor == null) {
                        connectionManager.broadcastOne(authToken, new ErrorMessage("ERROR: Observers cannot resign."));
                        break;
                    }
                    if (!updatedGame.getGameActive()) {
                        connectionManager.broadcastOne(authToken, new ErrorMessage("ERROR: The game is over."));
                        break;
                    }
                    updatedGame.endGame();
                    games.updateGame(new GameData(gameID, thisGame.whiteUsername(), thisGame.blackUsername(), thisGame.gameName(), updatedGame));
                    connectionManager.broadcastAllExcept(null, gameID, new NotificationMessage(userColor + " team (" + thisAuth.username() + ") resigned!"));
            }
//            System.out.println("END");
//            connectionManager.printConnections();
        } catch (DataAccessException e) {
            connectionManager.broadcastOne(authToken, new ErrorMessage("ERROR: " + e.getMessage()));
        }
    }
}

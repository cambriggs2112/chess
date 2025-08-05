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
    public WebSocketServer() {}

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        Gson gson = new Gson();
        UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
        String authToken = command.getAuthToken();
        int gameID = command.getGameID();
        try {
            SQLAuthDAO auths = new SQLAuthDAO();
            SQLGameDAO games = new SQLGameDAO();
            if (auths.getAuth(authToken) == null) {
                ConnectionManager.broadcastOne(authToken, new ErrorMessage("ERROR: Auth token is unknown."));
                return;
            }
            if (games.getGame(gameID) == null) {
                ConnectionManager.broadcastOne(authToken, new ErrorMessage("ERROR: Game ID is unknown."));
                return;
            }
            AuthData thisAuth = auths.getAuth(authToken);
            GameData thisGame = games.getGame(gameID);
            ChessGame updatedGame = thisGame.game();
            switch (command.getCommandType()) {
                case CONNECT:
                    ConnectCommand connCommand = gson.fromJson(message, ConnectCommand.class);
                    ConnectionManager.addConnection(authToken, gameID, session);
                    ConnectionManager.broadcastOne(authToken, new LoadGameMessage(thisGame.game()));
                    if (connCommand.getColor() == null) {
                        ConnectionManager.broadcastAllExcept(authToken, new NotificationMessage(thisAuth.username() + " joined as an observer."));
                    } else {
                        ConnectionManager.broadcastAllExcept(authToken, new NotificationMessage(thisAuth.username() + " joined as the " + connCommand.getColor() + " player."));
                    }
                    break;
                case MAKE_MOVE:
                    MakeMoveCommand moveCommand = gson.fromJson(message, MakeMoveCommand.class);
                    ChessMove move = moveCommand.getMove();
                    try {
                        updatedGame.makeMove(move);
                        games.updateGame(new GameData(gameID, thisGame.whiteUsername(), thisGame.blackUsername(), thisGame.gameName(), updatedGame));
                        ConnectionManager.broadcastAllExcept(null, new LoadGameMessage(updatedGame));
                        ConnectionManager.broadcastAllExcept(authToken, new NotificationMessage(thisAuth.username() + " made move " + move + "."));
                        if (updatedGame.isInCheckmate(ChessGame.TeamColor.WHITE)) {
                            ConnectionManager.broadcastAllExcept(null, new NotificationMessage("WHITE team (" + thisGame.whiteUsername() + ") is in checkmate!"));
                            updatedGame.endGame();
                        } else if (updatedGame.isInCheck(ChessGame.TeamColor.WHITE)) {
                            ConnectionManager.broadcastAllExcept(null, new NotificationMessage("WHITE team (" + thisGame.whiteUsername() + ") is in check!"));
                        } else if (updatedGame.isInStalemate(ChessGame.TeamColor.WHITE)) {
                            ConnectionManager.broadcastAllExcept(null, new NotificationMessage("WHITE team (" + thisGame.whiteUsername() + ") is in stalemate!"));
                            if (updatedGame.isInStalemate(ChessGame.TeamColor.BLACK)) {
                                updatedGame.endGame();
                            }
                        }
                        if (updatedGame.isInCheckmate(ChessGame.TeamColor.BLACK)) {
                            ConnectionManager.broadcastAllExcept(null, new NotificationMessage("BLACK team (" + thisGame.blackUsername() + ") is in checkmate!"));
                            updatedGame.endGame();
                        } else if (updatedGame.isInCheck(ChessGame.TeamColor.BLACK)) {
                            ConnectionManager.broadcastAllExcept(null, new NotificationMessage("BLACK team (" + thisGame.blackUsername() + ") is in check!"));
                        } else if (updatedGame.isInStalemate(ChessGame.TeamColor.BLACK)) {
                            ConnectionManager.broadcastAllExcept(null, new NotificationMessage("BLACK team (" + thisGame.blackUsername() + ") is in stalemate!"));
                            if (updatedGame.isInStalemate(ChessGame.TeamColor.WHITE)) {
                                updatedGame.endGame();
                            }
                        }
                    } catch (InvalidMoveException e) {
                        ConnectionManager.broadcastOne(authToken, new ErrorMessage("ERROR: " + e.getMessage()));
                    }
                    // check move -> if valid, update and send board to all
                    break;
                case LEAVE:
                    LeaveCommand leaveCommand = gson.fromJson(message, LeaveCommand.class);
                    ConnectionManager.removeConnection(authToken);
                    ConnectionManager.broadcastAllExcept(authToken, new NotificationMessage(thisAuth.username() + " left the game."));
                    if (leaveCommand.getColor() == ChessGame.TeamColor.WHITE) {
                        games.updateGame(new GameData(gameID, null, thisGame.blackUsername(), thisGame.gameName(), thisGame.game()));
                    } else {
                        games.updateGame(new GameData(gameID, thisGame.whiteUsername(), null, thisGame.gameName(), thisGame.game()));
                    }
                    break;
                case RESIGN:
                    ResignCommand resignCommand = gson.fromJson(message, ResignCommand.class);
                    updatedGame.endGame();
                    games.updateGame(new GameData(gameID, thisGame.whiteUsername(), thisGame.blackUsername(), thisGame.gameName(), updatedGame));
                    ConnectionManager.broadcastAllExcept(null, new NotificationMessage(resignCommand.getColor() + " team (" + thisAuth.username() + ") resigned!"));
            }
        } catch (DataAccessException e) {
            ConnectionManager.broadcastOne(authToken, new ErrorMessage("ERROR: " + e.getMessage()));
        }
    }
}

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
            switch (command.getCommandType()) {
                case CONNECT:
                    ConnectCommand connCommand = gson.fromJson(message, ConnectCommand.class);
                    ConnectionManager.addConnection(authToken, gameID, session);
                    ConnectionManager.broadcastOne(authToken, new LoadGameMessage(thisGame.game()));
                    if (connCommand.getColor() == null) {
                        ConnectionManager.broadcastAllExcept(authToken, new NotificationMessage(thisAuth.username() + " joined as observer"));
                    } else {
                        ConnectionManager.broadcastAllExcept(authToken, new NotificationMessage(thisAuth.username() + " joined as " + connCommand.getColor() + " player"));
                    }
                    break;
                case MAKE_MOVE:
                    MakeMoveCommand moveCommand = gson.fromJson(message, MakeMoveCommand.class);

                    // check move -> if valid, update and send board to all
                    break;
                case LEAVE:
                    LeaveCommand leaveCommand = gson.fromJson(message, LeaveCommand.class);
                    ConnectionManager.removeConnection(authToken);
                    ConnectionManager.broadcastAllExcept(authToken, new NotificationMessage(thisAuth.username() + " left the game"));
                    if (leaveCommand.getColor() == ChessGame.TeamColor.WHITE) {
                        games.updateGame(new GameData(gameID, null, thisGame.blackUsername(), thisGame.gameName(), thisGame.game()));
                    } else {
                        games.updateGame(new GameData(gameID, thisGame.whiteUsername(), null, thisGame.gameName(), thisGame.game()));
                    }
                case RESIGN:
                    ResignCommand resignCommand = gson.fromJson(message, ResignCommand.class);
                    // stop game and notify all
            }
        } catch (DataAccessException e) {
            ConnectionManager.broadcastOne(authToken, new ErrorMessage("ERROR: " + e.getMessage()));
        }
    }
}

package server;

import com.google.gson.Gson;
import dataaccess.*;
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
        try {
            SQLAuthDAO auths = new SQLAuthDAO();
            SQLGameDAO games = new SQLGameDAO();
            if (auths.getAuth(command.getAuthToken()) == null) {
                session.getRemote().sendString(gson.toJson(new ErrorMessage("ERROR: Auth token is unknown.")));
                return;
            }
            if (games.getGame(command.getGameID()) == null) {
                session.getRemote().sendString(gson.toJson(new ErrorMessage("ERROR: Game ID is unknown.")));
                return;
            }
            switch (command.getCommandType()) {
                case CONNECT:
                    ConnectionManager.addConnection(command.getAuthToken(), command.getGameID(), session);
                    // send board, notify everyone else
                case MAKE_MOVE:
                    MakeMoveCommand moveCommand = gson.fromJson(message, MakeMoveCommand.class);
                    // check move -> if valid, update and send board to all
                case LEAVE:
                    ConnectionManager.removeConnection(command.getAuthToken());
                case RESIGN:
                    // stop game and notify all
            }
        } catch (DataAccessException e) {
            session.getRemote().sendString(gson.toJson(new ErrorMessage("ERROR: Unable to perform command:" + e.getMessage())));
        }
    }
}

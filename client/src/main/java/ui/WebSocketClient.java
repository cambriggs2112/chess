package ui;

import javax.websocket.*;
import java.net.*;
import java.io.*;
import java.util.ArrayList;

import com.google.gson.Gson;
import websocket.commands.*;
import websocket.messages.*;
import chess.*;

public class WebSocketClient extends Endpoint {
    private Session session;

    public WebSocketClient(String url, ChessGame.TeamColor color, String username, String gameName)
            throws URISyntaxException, DeploymentException, IOException {
        this.session = ContainerProvider.getWebSocketContainer().connectToServer(this, new URI(url));
        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message) {
                Gson gson = new Gson();
                ServerMessage sm = gson.fromJson(message, ServerMessage.class);
                switch (sm.getServerMessageType()) {
                    case LOAD_GAME:
                        LoadGameMessage lgm = gson.fromJson(message, LoadGameMessage.class);
                        ClientMainFuncs.updateGameObj(lgm.getGame());
                        ChessMove gameMove = lgm.getMove();
                        ArrayList<ChessMove> moves = new ArrayList<ChessMove>();
                        if (gameMove != null) {
                            moves.add(gameMove);
                        }
                        System.out.println();
                        ClientMainFuncs.printBoard(color, moves);
                        break;
                    case ERROR:
                        ErrorMessage em = gson.fromJson(message, ErrorMessage.class);
                        System.out.println("\u001b[38;5;160m" + em.getErrorMessage() + "\u001b[39m");
                        break;
                    case NOTIFICATION:
                        NotificationMessage nm = gson.fromJson(message, NotificationMessage.class);
                        System.out.println("\u001b[38;5;46m" + nm.getMessage() + "\u001b[39m");
                        break;
                }
                System.out.print("[" + username + "." + gameName + "] >>> ");
            }
        });
    }

    public void onOpen(Session session, EndpointConfig endPointConfig) {}

    public void sendCommand(UserGameCommand command) {
        try {
            Gson gson = new Gson();
            this.session.getBasicRemote().sendText(gson.toJson(command));
        } catch (IOException e) {
            System.out.println("\u001b[38;5;160mERROR: " + e.getMessage() + "\u001b[39m");
        }
    }

    public void close() {
        try {
            this.session.close();
        } catch (IOException e) {
            System.out.println("\u001b[38;5;160mERROR: " + e.getMessage() + "\u001b[39m");
        }
    }
}

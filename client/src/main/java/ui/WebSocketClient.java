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
    private static ChessGame gameObj = null;
    // Consider adding move here

    public WebSocketClient(String url, ChessGame.TeamColor color) throws URISyntaxException, DeploymentException, IOException {
        this.session = ContainerProvider.getWebSocketContainer().connectToServer(this, new URI(url));
        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message) {
                Gson gson = new Gson();
                ServerMessage sm = gson.fromJson(message, ServerMessage.class);
                switch (sm.getServerMessageType()) {
                    case LOAD_GAME:
                        LoadGameMessage lgm = gson.fromJson(message, LoadGameMessage.class);
                        gameObj = lgm.getGame();
                        ChessMove gameMove = lgm.getMove();
                        ArrayList<ChessMove> moves = new ArrayList<ChessMove>();
                        if (gameMove != null) {
                            moves.add(gameMove);
                        }
                        if (color == null) {
                            ClientMainFuncs.printBoard(ChessGame.TeamColor.WHITE, lgm.getGame(), moves);
                        } else {
                            ClientMainFuncs.printBoard(color, lgm.getGame(), moves);
                        }
                        break;
                    case ERROR:
                        ErrorMessage em = gson.fromJson(message, ErrorMessage.class);
                        System.out.println("\u001b[38;5;160m  " + em.getErrorMessage() + "\u001b[39m");
                        break;
                    case NOTIFICATION:
                        NotificationMessage nm = gson.fromJson(message, NotificationMessage.class);
                        System.out.println("\u001b[38;5;46m  " + nm.getMessage() + "\u001b[39m");
                        break;
                }
            }
        });
    }

    public ChessGame getGame() {
        return gameObj;
    }

    public void onOpen(Session session, EndpointConfig endPointConfig) {}

    public void sendCommand(UserGameCommand command) {
        try {
            Gson gson = new Gson();
            this.session.getBasicRemote().sendText(gson.toJson(command));
        } catch (IOException e) {
            System.out.println("\u001b[38;5;160m  ERROR: " + e.getMessage() + "\u001b[39m");
        }
    }

    public void close() {
        try {
            this.session.close();
        } catch (IOException e) {
            System.out.println("\u001b[38;5;160m  ERROR: " + e.getMessage() + "\u001b[39m");
        }
    }
}

package ui;

import javax.websocket.*;
import java.net.*;
import java.io.*;
import com.google.gson.Gson;
import websocket.commands.*;
import websocket.messages.*;
import chess.*;

public class WebSocketClient extends Endpoint {
    private Session session;

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
                        if (color == null) {
                            ClientMainFuncs.printBoard(ChessGame.TeamColor.WHITE, lgm.getGame());
                        } else {
                            ClientMainFuncs.printBoard(color, lgm.getGame());
                        }
                        break;
                    case ERROR:
                        ErrorMessage em = gson.fromJson(message, ErrorMessage.class);
                        System.out.println("\u001b[38;5;160m  " + em.getErrorMessage());
                        break;
                    case NOTIFICATION:
                        NotificationMessage nm = gson.fromJson(message, NotificationMessage.class);
                        System.out.println("\u001b[38;5;46m  " + nm.getMessage());
                        break;
                }
            }
        });
    }

    public void onOpen(Session session, EndpointConfig endPointConfig) {}

    public void sendCommand(UserGameCommand command) throws IOException {
        Gson gson = new Gson();
        this.session.getBasicRemote().sendText(gson.toJson(command));
    }
}

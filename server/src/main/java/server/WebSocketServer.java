package server;

import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.*;
import java.io.*;

@WebSocket
public class WebSocketServer {
    public WebSocketServer() {}

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        // do something with message
        session.getRemote().sendString("str");
    }
}

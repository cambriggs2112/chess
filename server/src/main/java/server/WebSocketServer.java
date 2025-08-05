package server;

import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.*;
import java.io.*;

@WebSocket
public class WebSocketServer {
    public WebSocketServer() {}

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        // 1. De-serialize message to user command
        // 2. Check auth token and game ID
        // 3. Save session to Connection Manager
        // 4. Do something according to command type
        // 5. Serialize and send output
        session.getRemote().sendString("str");
    }

    public void connect() {

    }
}

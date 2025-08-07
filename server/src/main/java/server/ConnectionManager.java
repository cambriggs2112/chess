package server;

import java.util.HashMap;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.*;
import java.io.*;

public class ConnectionManager {
    private HashMap<String, Connection> connections = new HashMap<String, Connection>();

    public ConnectionManager() {}

    public void addConnection(String authToken, int gameID, Session session) {
        connections.put(authToken, new Connection(authToken, gameID, session));
    }

    public void broadcastOne(String authToken, ServerMessage message) {
        Gson gson = new Gson();
        Connection conn = connections.get(authToken);
        if (conn == null) {
            return;
        }
        try {
            conn.session().getRemote().sendString(gson.toJson(message));
        } catch (IOException e) {
            System.out.println(e.getMessage());
            removeConnection(authToken);
        }
    }

    public void broadcastAllExcept(String authToken, ServerMessage message) {
        for (Connection conn : connections.values()) {
            if (!conn.authToken().equals(authToken)) {
                broadcastOne(authToken, message);
            }
        }
    }

    public void removeConnection(String authToken) {
        connections.remove(authToken);
    }
}

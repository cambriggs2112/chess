package server;

import java.util.HashMap;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.*;
import java.io.*;

public class ConnectionManager {
    private static HashMap<String, Connection> connections = new HashMap<String, Connection>();

    public static void addConnection(String authToken, int gameID, Session session) {
        connections.put(authToken, new Connection(authToken, gameID, session));
    }

    public static void broadcastOne(String authToken, ServerMessage message) {
        Gson gson = new Gson();
        Connection conn = connections.get(authToken);
        if (conn == null) {
            return;
        }
        try (Session session = conn.session()) {
            if (session.isOpen()) {
                session.getRemote().sendString(gson.toJson(message));
            } else {
                removeConnection(authToken);
            }
        } catch (IOException e) {
            removeConnection(authToken);
        }
    }

    public static void broadcastAllExcept(String authToken, ServerMessage message) {
        for (Connection conn : connections.values()) {
            if (!conn.authToken().equals(authToken)) {
                broadcastOne(authToken, message);
            }
        }
    }

    public static void removeConnection(String authToken) {
        connections.remove(authToken);
    }
}

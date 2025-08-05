package server;

import java.util.HashMap;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.*;

public class ConnectionManager {
    private static HashMap<String, Connection> connections = new HashMap<String, Connection>();

    public static void addConnection(String authToken, int gameID, Session session) {
        connections.put(authToken, new Connection(authToken, gameID, session));
    }

    public static void broadcastOne(String authToken, ServerMessage message) {

    }

    public static void broadcastAllExcept(String authToken, ServerMessage message) {

    }

    public static void broadcastAll(ServerMessage message) {

    }

    public static void removeConnection(String authToken) {

    }
}

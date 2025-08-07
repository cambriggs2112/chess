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
            if (conn.session().isOpen()) {
                conn.session().getRemote().sendString(gson.toJson(message));
//                System.out.println("Sent " + message.getServerMessageType() + " message to " + authToken);
            } else {
                removeConnection(authToken);
            }
        } catch (IOException e) {
            removeConnection(authToken);
        }
    }

    public void broadcastAllExcept(String authToken, int gameID, ServerMessage message) {
        for (Connection conn : connections.values()) {
            if (!conn.authToken().equals(authToken) && conn.gameID() == gameID) {
                broadcastOne(conn.authToken(), message);
            }
        }
    }

    public void removeConnection(String authToken) {
        connections.get(authToken).session().close();
        connections.remove(authToken);
    }

//    public void printConnections() {
//        System.out.println("CONNECTIONS:");
//        for (String key : connections.keySet()) {
//            System.out.println(key);
//        }
//    }
}

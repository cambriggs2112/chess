package server;

import org.eclipse.jetty.websocket.api.Session;

public record Connection(String authToken, int gameID, Session session) {}

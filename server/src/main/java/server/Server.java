package server;

import spark.*;

/**
 * A server that processes HTTP requests/responses and performs services on its databases.
 */
public class Server {
    private ClearApplicationHandler clearApplicationHandler;
    private CreateGameHandler createGameHandler;
    private JoinGameHandler joinGameHandler;
    private ListGamesHandler listGamesHandler;
    private LoginHandler loginHandler;
    private LogoutHandler logoutHandler;
    private RegisterHandler registerHandler;

    public Server() {
        this.clearApplicationHandler = new ClearApplicationHandler();
        this.createGameHandler = new CreateGameHandler();
        this.joinGameHandler = new JoinGameHandler();
        this.listGamesHandler = new ListGamesHandler();
        this.loginHandler = new LoginHandler();
        this.logoutHandler = new LogoutHandler();
        this.registerHandler = new RegisterHandler();
    }

    /**
     * Generates a connection on a given port and starts the server loop.
     *
     * @param desiredPort the intended port
     * @return the actual port
     */
    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.webSocket("/ws", WebSocketServer.class);
        Spark.delete("/db", (req, res) -> clearApplicationHandler.handleRequest(req, res));
        Spark.post("/game", (req, res) -> createGameHandler.handleRequest(req, res));
        Spark.put("/game", (req, res) -> joinGameHandler.handleRequest(req, res));
        Spark.get("/game", (req, res) -> listGamesHandler.handleRequest(req, res));
        Spark.post("/session", (req, res) -> loginHandler.handleRequest(req, res));
        Spark.delete("/session", (req, res) -> logoutHandler.handleRequest(req, res));
        Spark.post("/user", (req, res) -> registerHandler.handleRequest(req, res));

        Spark.awaitInitialization();
        return Spark.port();
    }

    /**
     * Stops the server loop
     */
    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}

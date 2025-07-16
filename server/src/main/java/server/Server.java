package server;

import spark.*;
import dataaccess.*;
import service.*;

/**
 * A server that processes HTTP requests/responses and performs services on its databases.
 */
public class Server {
    private AuthDAO auths;
    private GameDAO games;
    private UserDAO users;
    private ClearApplicationHandler clearApplicationHandler;
    private CreateGameHandler createGameHandler;
    private JoinGameHandler joinGameHandler;
    private ListGamesHandler listGamesHandler;
    private LoginHandler loginHandler;
    private LogoutHandler logoutHandler;
    private RegisterHandler registerHandler;
    private ClearApplicationService clearApplicationService;
    private CreateGameService createGameService;
    private JoinGameService joinGameService;
    private ListGamesService listGamesService;
    private LoginService loginService;
    private LogoutService logoutService;
    private RegisterService registerService;

    public Server() {
        this.auths = new MemoryAuthDAO();
        this.games = new MemoryGameDAO();
        this.users = new MemoryUserDAO();
        this.clearApplicationService = new ClearApplicationService(auths, games, users);
        this.createGameService = new CreateGameService(auths, games, users);
        this.joinGameService = new JoinGameService(auths, games, users);
        this.listGamesService = new ListGamesService(auths, games, users);
        this.loginService = new LoginService(auths, games, users);
        this.logoutService = new LogoutService(auths, games, users);
        this.registerService = new RegisterService(auths, games, users);
        this.clearApplicationHandler = new ClearApplicationHandler(clearApplicationService);
        this.createGameHandler = new CreateGameHandler(createGameService);
        this.joinGameHandler = new JoinGameHandler(joinGameService);
        this.listGamesHandler = new ListGamesHandler(listGamesService);
        this.loginHandler = new LoginHandler(loginService);
        this.logoutHandler = new LogoutHandler(logoutService);
        this.registerHandler = new RegisterHandler(registerService);
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

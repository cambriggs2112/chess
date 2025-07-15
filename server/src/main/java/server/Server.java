package server;

import spark.*;
import dataaccess.*;
import service.*;

public class Server {
    private AuthDAO auths;
    private GameDAO games;
    private UserDAO users;
    private ClearApplicationHandler clearApplicationHandler;
    private CreateGameHandler createGameHandler;
    private JoinGameHandler joingameHandler;
    private ListGamesHandler listGamesHandler;
    private LoginHandler loginHandler;
    private LogoutHandler logoutHandler;
    private RegisterHandler registerHandler;
    private ClearApplicationService clearApplicationService;
    private CreateGameService createGameService;
    private JoinGameService joingameService;
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
        this.joingameService = new JoinGameService(auths, games, users);
        this.listGamesService = new ListGamesService(auths, games, users);
        this.loginService = new LoginService(auths, games, users);
        this.logoutService = new LogoutService(auths, games, users);
        this.registerService = new RegisterService(auths, games, users);
        this.clearApplicationHandler = new ClearApplicationHandler(clearApplicationService);
        this.createGameHandler = new CreateGameHandler(createGameService);
        this.joingameHandler = new JoinGameHandler(joingameService);
        this.listGamesHandler = new ListGamesHandler(listGamesService);
        this.loginHandler = new LoginHandler(loginService);
        this.logoutHandler = new LogoutHandler(logoutService);
        this.registerHandler = new RegisterHandler(registerService);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", (req, res) -> clearApplicationHandler.handleRequest(req, res));
        Spark.post("/user", (req, res) -> registerHandler.handleRequest(req, res));

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }


}

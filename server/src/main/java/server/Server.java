package server;

import spark.*;

public class Server {
    public static class ClearApplicationHandler {}

    public static class CreateGameHandler {}

    public static class JoinGameHandler {}

    public static class ListGamesHandler {}

    public static class LoginHandler {}

    public static class LogoutHandler {}

    public static class RegisterHandler {}

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

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

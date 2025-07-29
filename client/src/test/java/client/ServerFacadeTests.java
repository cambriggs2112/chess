package client;

import model.Request.*;
import model.Result.CreateGameResult;
import model.Result.ListGamesResult;
import model.Result.LoginResult;
import model.Result.RegisterResult;
import org.junit.jupiter.api.*;
import server.Server;
import ui.*;
import model.*;
import chess.*;
import java.util.UUID;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade sf = null;
    private static String testUsername = "testUser";
    private static String testPassword = "testPass";
    private static String testEmail = "test@gmail.com";
    private static String testGameName = "test game";

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        sf = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    @Order(1)
    public void clearOnce() {
        try {
            sf.clearApplication();
        } catch (ServiceException e) {
            Assertions.fail("Clear Application should not throw exceptions.");
        }
    }

    @Test
    @Order(2)
    public void registerNormally() {
        clearSetup();
        try {
            RegisterResult res = sf.register(new RegisterRequest(testUsername, testPassword, testEmail));
            Assertions.assertNotNull(res, "Register returned a null object");
            Assertions.assertNotNull(res.authToken(), "The return object has no auth token");
            Assertions.assertNotNull(res.username(), "The return object has no username");
            Assertions.assertEquals(testUsername, res.username(), "The usernames do not match");
        } catch (ServiceException e) {
            Assertions.fail("Register should not throw exceptions.");
        }
    }

    @Test
    @Order(3)
    public void registerUsernameAlreadyExists() {
        RegisterResult res = null;
        registerSetup();
        try {
            res = sf.register(new RegisterRequest(testUsername, "otherPass", "other@gmail.com"));
            Assertions.fail("Register should have thrown an exception.");
        } catch (ServiceException e) {
            Assertions.assertTrue(e.getMessage().toLowerCase().contains("username"),
                    "Error message did not mention a problem with the username.");
            Assertions.assertEquals(403, e.getHTTPCode(),
                    "Register returned the wrong HTTP code.");
            Assertions.assertNull(res, "Return object should be null.");
        }
    }

    @Test
    @Order(4)
    public void loginNormally() {
        registerSetup();
        try {
            LoginResult res = sf.login(new LoginRequest(testUsername, testPassword));
            Assertions.assertNotNull(res, "Login returned a null object");
            Assertions.assertNotNull(res.authToken(), "The return object has no auth token");
            Assertions.assertNotNull(res.username(), "The return object has no username");
            Assertions.assertEquals(testUsername, res.username(), "The usernames do not match");
        } catch (ServiceException e) {
            Assertions.fail("Login should not throw exceptions.");
        }
    }

    @Test
    @Order(5)
    public void loginWrongInformation() {
        LoginResult res = null;
        registerSetup();
        try {
            res = sf.login(new LoginRequest("wrongUser", testPassword));
            Assertions.fail("Login should have thrown an exception.");
        } catch (ServiceException e) {
            Assertions.assertTrue(e.getMessage().toLowerCase().contains("username"),
                    "Error message did not mention a problem with the username.");
            Assertions.assertEquals(401, e.getHTTPCode(),
                    "Login returned the wrong HTTP code.");
            Assertions.assertNull(res, "Return object should be null.");
        }
        try {
            res = sf.login(new LoginRequest(testUsername, "wrongPass"));
            Assertions.fail("Login should have thrown an exception.");
        } catch (ServiceException e) {
            Assertions.assertTrue(e.getMessage().toLowerCase().contains("password"),
                    "Error message did not mention a problem with the password.");
            Assertions.assertEquals(401, e.getHTTPCode(),
                    "Login returned the wrong HTTP code.");
            Assertions.assertNull(res, "Return object should be null.");
        }
    }

    @Test
    @Order(6)
    public void createGameNormally() {
        String testAuthToken = registerSetup();
        try {
            CreateGameResult res = sf.createGame(new CreateGameRequest(testAuthToken, testGameName));
            Assertions.assertNotNull(res, "Create Game returned a null object");
            Assertions.assertNotNull(res.gameID(), "The return object has no game ID");
            Assertions.assertTrue(res.gameID() > 0, "The game ID is invalid");
        } catch (ServiceException e) {
            Assertions.fail("Create Game should not throw exceptions. " +
                    "If List Games is correct, Login or Register may be returning the wrong auth token.");
        }
    }

    @Test
    @Order(7)
    public void createGameBadAuth() {
        CreateGameResult res = null;
        try {
            res = sf.createGame(new CreateGameRequest("wrong-auth-token", testGameName));
            Assertions.fail("Create Game should have thrown an exception.");
        } catch (ServiceException e) {
            Assertions.assertTrue(e.getMessage().toLowerCase().contains("auth"),
                    "Error message did not mention a problem with the auth token.");
            Assertions.assertEquals(401, e.getHTTPCode(),
                    "Create Game returned the wrong HTTP code.");
            Assertions.assertNull(res, "Return object should be null.");
        }
    }

    @Test
    @Order(8)
    public void listGamesNormally() {
        String testAuthToken = registerSetup();
        int testGameID = createGameSetup(testAuthToken);
        try {
            ListGamesResult res = sf.listGames(new ListGamesRequest(testAuthToken));
            Assertions.assertNotNull(res, "List Games returned a null object");
            Assertions.assertNotNull(res.games(), "List Games did not contain a list of objects");
            Assertions.assertEquals(1, res.games().size(), "List Games did not contain exactly 1 element");
            Assertions.assertNotNull(res.games().getFirst().gameID(), "The object in the list had no game ID");
            Assertions.assertNotNull(res.games().getFirst().gameName(), "The object in the list had no game name");
            Assertions.assertNull(res.games().getFirst().whiteUsername(), "The object in the list had a white player");
            Assertions.assertNull(res.games().getFirst().blackUsername(), "The object in the list had a black player");
            Assertions.assertEquals(testGameID, res.games().getFirst().gameID(), "The game IDs do not match");
            Assertions.assertEquals(testGameName, res.games().getFirst().gameName(), "The game names do not match");
            sf.createGame(new CreateGameRequest(testAuthToken, "other game"));
            res = sf.listGames(new ListGamesRequest(testAuthToken));
            Assertions.assertEquals(2, res.games().size(), "List Games did not contain exactly 2 elements");
        } catch (ServiceException e) {
            Assertions.fail("List Games (and Create Game) should not throw exceptions. " +
                    "If List Games is correct, Login or Register may be returning the wrong auth token.");
        }
    }

    @Test
    @Order(9)
    public void listGamesBadAuth() {
        ListGamesResult res = null;
        try {
            res = sf.listGames(new ListGamesRequest("wrong-auth-token"));
            Assertions.fail("List Games should have thrown an exception.");
        } catch (ServiceException e) {
            Assertions.assertTrue(e.getMessage().toLowerCase().contains("auth"),
                    "Error message did not mention a problem with the auth token.");
            Assertions.assertEquals(401, e.getHTTPCode(),
                    "List Games returned the wrong HTTP code.");
            Assertions.assertNull(res, "Return object should be null.");
        }
    }

    @Test
    @Order(10)
    public void joinGameNormally() {
        String testAuthToken = registerSetup();
        int testGameID = createGameSetup(testAuthToken);
        try {
            sf.joinGame(new JoinGameRequest(testAuthToken, ChessGame.TeamColor.WHITE, testGameID));
            ListGamesResult res = sf.listGames(new ListGamesRequest(testAuthToken));
            Assertions.assertNotNull(res, "List Games returned a null object whilst attempting to verify Join Game");
            Assertions.assertNotNull(res.games(), "List Games did not contain a list of objects whilst attempting to verify Join Game");
            Assertions.assertEquals(1, res.games().size(), "List Games did not contain exactly 1 element whilst attempting to verify Join Game");
            Assertions.assertNotNull(res.games().getFirst().gameID(), "The object in the list had no game ID whilst attempting to verify Join Game");
            Assertions.assertEquals(testGameID, res.games().getFirst().gameID(), "The game IDs did not match whilst attempting to verify Join Game");
            Assertions.assertNotNull(res.games().getFirst().whiteUsername(), "The object in the list had no white player");
            Assertions.assertEquals(testUsername, res.games().getFirst().whiteUsername(), "The white usernames do not match");
        } catch (ServiceException e) {
            Assertions.fail("Join Game (and List Games) should not throw exceptions. " +
                    "If Join Game is correct, Login or Register may be returning the wrong auth token " +
                    "or Create Game may be returning the wrong game ID.");
        }
    }

    @Test
    @Order(11)
    public void joinGameAlreadyTaken() {
        String testAuthToken = registerSetup();
        int testGameID = createGameSetup(testAuthToken);
        joinGameSetup(testAuthToken, testGameID);
        try {
            sf.joinGame(new JoinGameRequest(testAuthToken, ChessGame.TeamColor.WHITE, testGameID));
            Assertions.fail("Join Game should have thrown an exception.");
        } catch (ServiceException e) {
            Assertions.assertTrue(e.getMessage().toLowerCase().contains("taken"),
                    "Error message did not mention a problem with a team being already taken.");
            Assertions.assertEquals(403, e.getHTTPCode(),
                    "Join Game returned the wrong HTTP code.");
        }
    }

    @Test
    @Order(12)
    public void joinGameBadID() {
        String testAuthToken = registerSetup();
        int random = 0;
        while (random <= 0) {
            random = Math.abs(UUID.randomUUID().hashCode()); // guaranteed random number greater than 0
        }
        try {
            sf.joinGame(new JoinGameRequest(testAuthToken, ChessGame.TeamColor.WHITE, random));
            Assertions.fail("Join Game should have thrown an exception.");
        } catch (ServiceException e) {
            Assertions.assertTrue(e.getMessage().toLowerCase().contains("id"),
                    "Error message did not mention a problem with the game ID.");
            Assertions.assertEquals(403, e.getHTTPCode(),
                    "Join Game returned the wrong HTTP code.");
        }
    }

    @Test
    @Order(13)
    public void logoutNormally() {
        String testAuthToken = registerSetup();
        int testGameID = createGameSetup(testAuthToken);
        try {
            sf.logout(new LogoutRequest(testAuthToken));
        } catch (ServiceException e) {
            Assertions.fail("Logout should not throw exceptions. " +
                    "If Logout is correct, Login or Register may be returning the wrong auth token.");
        }
        try {
            sf.createGame(new CreateGameRequest(testAuthToken, testGameName));
            Assertions.fail("Create Game should not work after logout.");
        } catch (ServiceException e) {}
        try {
            sf.listGames(new ListGamesRequest(testAuthToken));
            Assertions.fail("List Games should not work after logout.");
        } catch (ServiceException e) {}
        try {
            sf.joinGame(new JoinGameRequest(testAuthToken, ChessGame.TeamColor.BLACK, testGameID));
            Assertions.fail("Join Game should not work after logout.");
        } catch (ServiceException e) {}
    }

    @Test
    @Order(14)
    public void logoutBadAuth() {
        try {
            sf.logout(new LogoutRequest("wrong-auth-token"));
            Assertions.fail("Logout should have thrown an exception.");
        } catch (ServiceException e) {
            Assertions.assertTrue(e.getMessage().toLowerCase().contains("auth"),
                    "Error message did not mention a problem with the auth token.");
            Assertions.assertEquals(401, e.getHTTPCode(),
                    "Logout returned the wrong HTTP code.");
        }
    }

    @Test
    @Order(15)
    public void clearMultipleTimes() {
        try {
            sf.clearApplication();
            sf.clearApplication();
            sf.clearApplication();
        } catch (ServiceException e) {
            Assertions.fail("Clear Application should not throw exceptions.");
        }
        try {
            sf.login(new LoginRequest(testUsername, testPassword));
            Assertions.fail("Login should not work after clear.");
        } catch (ServiceException e) {}
    }

    private static void clearSetup() {
        try {
            sf.clearApplication();
        } catch (ServiceException e) {
            Assertions.fail("Setup should not throw exceptions.");
        }
    }

    private static String registerSetup() {
        clearSetup();
        try {
            return sf.register(new RegisterRequest(testUsername, testPassword, testEmail)).authToken();
        } catch (ServiceException e) {
            Assertions.fail("Setup should not throw exceptions.");
            return null;
        }
    }

    private static int createGameSetup(String testAuthToken) {
        try {
            return sf.createGame(new CreateGameRequest(testAuthToken, testGameName)).gameID();
        } catch (ServiceException e) {
            Assertions.fail("Setup should not throw exceptions.");
            return 0;
        }
    }

    private static void joinGameSetup(String testAuthToken, int testGameID) {
        try {
            sf.joinGame(new JoinGameRequest(testAuthToken, ChessGame.TeamColor.WHITE, testGameID));
        } catch (ServiceException e) {
            Assertions.fail("Setup should not throw exceptions.");
        }
    }
}

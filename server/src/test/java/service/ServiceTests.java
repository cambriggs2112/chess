package service;

import chess.ChessGame;
import dataaccess.*;
import model.*;
import model.request.*;
import model.result.*;
import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;

public class ServiceTests {
    private static SQLAuthDAO auths;
    private static SQLGameDAO games;
    private static SQLUserDAO users;
    private static UserData existingUser;
    private static GameData existingGame;
    private static AuthData existingAuth;

    @BeforeEach
    public void setupDAOs() {
        try {
            auths = new SQLAuthDAO();
            games = new SQLGameDAO();
            users = new SQLUserDAO();
            // Add existing data to databases (assume a user registered, created a game, and joined as white)
            existingAuth = new AuthData("9ddacd1c-778b-9a6c-468f-bf7bc268b967", "user");
            existingGame = new GameData(1234, "user", null, "game", new ChessGame());
            existingUser = new UserData("user", BCrypt.hashpw("pass", BCrypt.gensalt()), "user@gmail.com");
            auths.clear();
            games.clear();
            users.clear();
            auths.createAuth(existingAuth);
            games.createGame(existingGame);
            users.createUser(existingUser);
        } catch (DataAccessException e) {
            Assertions.fail("Data Access methods should not throw exceptions. " + e);
        }
    }

    // Helper method
    private void checkEmptyDatabase() {
        try {
            Assertions.assertNotNull(auths.listAuths(),
                    "Auth list should not be null.");
            Assertions.assertNotNull(games.listGames(),
                    "Game list should not be null.");
            Assertions.assertNotNull(users.listUsers(),
                    "User list should not be null.");
            Assertions.assertEquals(0, auths.listAuths().size(),
                    "Auth list should be empty.");
            Assertions.assertEquals(0, games.listGames().size(),
                    "Game list should be empty.");
            Assertions.assertEquals(0, users.listUsers().size(),
                    "User list should be empty.");
        } catch (DataAccessException e) {
            Assertions.fail("Data Access methods should not throw exceptions. " + e);
        }
    }

    @Test
    public void clearApplicationOnce() {
        ClearApplicationService service = new ClearApplicationService();
        ClearApplicationRequest req = new ClearApplicationRequest();
        try {
            service.clearApplication(req);
            checkEmptyDatabase();
        } catch (ServiceException e) {
            Assertions.fail("Clear Application should not throw exceptions. " + e);
        }
    }

    @Test
    public void clearApplicationTwice() {
        ClearApplicationService service = new ClearApplicationService();
        ClearApplicationRequest req = new ClearApplicationRequest();
        try {
            service.clearApplication(req);
            service.clearApplication(req);
            checkEmptyDatabase();
        } catch (ServiceException e) {
            Assertions.fail("Clear Application should not throw exceptions. " + e);
        }
    }

    @Test
    public void createGameNormally() {
        CreateGameService service = new CreateGameService();
        CreateGameRequest req = new CreateGameRequest(
                existingAuth.authToken(), "chess");
        CreateGameResult res = null;
        try {
            res = service.createGame(req);
        } catch (ServiceException e) {
            Assertions.fail("Create Game should not throw exceptions. " + e);
        }
        try {
            Assertions.assertNotNull(games.listGames(),
                    "Game list should not be null.");
            Assertions.assertEquals(2, games.listGames().size(),
                    "Game list should have a second game.");
            Assertions.assertNotNull(res,
                    "The result object should not be null.");
            Assertions.assertNotNull(res.gameID(),
                    "Create Game should return a game ID.");
        } catch (DataAccessException e) {
            Assertions.fail("Data Access methods should not throw exceptions. " + e);
        }
    }

    @Test
    public void createGameWithoutAuth() {
        CreateGameService service = new CreateGameService();
        CreateGameRequest req = new CreateGameRequest(
                "incorrect-auth-token", "chess");
        try {
            service.createGame(req);
            Assertions.fail("Create Game should have thrown an exception.");
        } catch (ServiceException e) {
            Assertions.assertEquals(401, e.getHTTPCode(), "Create Game did not return the correct HTTP code.");
        }
        try {
            Assertions.assertNotNull(games.listGames(),
                    "Game list should not be null.");
            Assertions.assertEquals(1, games.listGames().size(),
                    "Game list should have one game.");
        } catch (DataAccessException e) {
            Assertions.fail("Data Access methods should not throw exceptions. " + e);
        }
    }

    @Test
    public void joinGameNormally() { // Note: WHITE is taken.
        JoinGameService service = new JoinGameService();
        JoinGameRequest req = new JoinGameRequest(
                existingAuth.authToken(), ChessGame.TeamColor.BLACK, existingGame.gameID());
        try {
            service.joinGame(req);
        } catch (ServiceException e) {
            Assertions.fail("Join Game should not throw exceptions. " + e);
        }
        try {
            Assertions.assertNotNull(games.getGame(existingGame.gameID()),
                    "Modified game should exist.");
            Assertions.assertNotNull(games.getGame(existingGame.gameID()).blackUsername(),
                    "Modified game should have someone playing as BLACK.");
            Assertions.assertNotNull(games.getGame(existingGame.gameID()).whiteUsername(),
                    "Modified game should still have someone playing as WHITE.");
            Assertions.assertEquals(existingUser.username(), games.getGame(existingGame.gameID()).blackUsername(),
                    "Modified game does not have the correct BLACK username.");
            Assertions.assertEquals(existingUser.username(), games.getGame(existingGame.gameID()).whiteUsername(),
                    "Modified game does not have the correct WHITE username.");
        } catch (DataAccessException e) {
            Assertions.fail("Data Access methods should not throw exceptions. " + e);
        }
    }

    @Test
    public void joinGameAsWhite() { // Note: WHITE is taken.
        JoinGameService service = new JoinGameService();
        JoinGameRequest req = new JoinGameRequest(
                existingAuth.authToken(), ChessGame.TeamColor.WHITE, existingGame.gameID());
        try {
            service.joinGame(req);
            Assertions.fail("Join Game should have thrown an exception.");
        } catch (ServiceException e) {
            Assertions.assertEquals(403, e.getHTTPCode(),
                    "Join Game did not return the correct HTTP code.");
        }
        try {
            Assertions.assertNotNull(games.getGame(existingGame.gameID()),
                    "Modified game should exist.");
            Assertions.assertNull(games.getGame(existingGame.gameID()).blackUsername(),
                    "Modified game should not have anyone playing as BLACK.");
            Assertions.assertNotNull(games.getGame(existingGame.gameID()).whiteUsername(),
                    "Modified game should still have someone playing as WHITE.");
            Assertions.assertEquals(existingUser.username(), games.getGame(existingGame.gameID()).whiteUsername(),
                    "Modified game does not have the correct WHITE username.");
        } catch (DataAccessException e) {
            Assertions.fail("Data Access methods should not throw exceptions. " + e);
        }
    }

    @Test
    public void listGamesNormally() {
        ListGamesService service = new ListGamesService();
        ListGamesRequest req = new ListGamesRequest(
                existingAuth.authToken());
        ListGamesResult res = null;
        try {
            res = service.listGames(req);
        } catch (ServiceException e) {
            Assertions.fail("List Games should not throw exceptions. " + e);
        }
        Assertions.assertNotNull(res,
                "Result object should not be null.");
        ArrayList<ListGamesResultElement> gamesList = res.games();
        Assertions.assertNotNull(gamesList,
                "Result list should not be null.");
        Assertions.assertEquals(1, gamesList.size(),
                "There should be one game in the list.");
        Assertions.assertNotNull(gamesList.get(0),
                "Game in list should not be null.");
        Assertions.assertEquals(existingGame.gameID(), gamesList.get(0).gameID(),
                "Game in list had incorrect game ID.");
        Assertions.assertEquals(existingGame.whiteUsername(), gamesList.get(0).whiteUsername(),
                "Game in list had incorrect white username.");
        Assertions.assertNull(gamesList.get(0).blackUsername(),
                "Game in list had incorrect black username.");
        Assertions.assertEquals(existingGame.gameName(), gamesList.get(0).gameName(),
                "Game in list had incorrect game name.");
    }

    @Test
    public void listGamesWithoutAuth() {
        ListGamesService service = new ListGamesService();
        ListGamesRequest req = new ListGamesRequest(
                "incorrect-auth-token");
        try {
            service.listGames(req);
            Assertions.fail("List Games should have thrown an exception.");
        } catch (ServiceException e) {
            Assertions.assertEquals(401, e.getHTTPCode(), "List Games did not return the correct HTTP code.");
        }
    }

    @Test
    public void loginNormally() {
        LoginService service = new LoginService();
        LoginRequest req = new LoginRequest(
                existingUser.username(), "pass"); // existingUser.password() is the hashed equivalent
        LoginResult res = null;
        try {
            res = service.login(req);
        } catch (ServiceException e) {
            Assertions.fail("Login should not throw exceptions. " + e);
        }
        try {
            Assertions.assertNotNull(res,
                    "Result object should not be null.");
            Assertions.assertNotNull(res.authToken(),
                    "Result object had no auth token.");
            Assertions.assertNotNull(res.username(),
                    "Result object had no username.");
            Assertions.assertEquals(existingUser.username(), res.username(),
                    "Result object did not contain the correct username.");
            Assertions.assertNotNull(auths.listAuths(),
                    "Auths list should not be null.");
            Assertions.assertEquals(2, auths.listAuths().size(),
                    "Auths list should contain two auths.");
        } catch (DataAccessException e) {
            Assertions.fail("Data Access methods should not throw exceptions. " + e);
        }
    }

    @Test
    public void loginIncorrectPassword() {
        LoginService service = new LoginService();
        LoginRequest req = new LoginRequest(
                existingUser.username(), "incorrect-password");
        try {
            service.login(req);
            Assertions.fail("Login should have thrown an exception.");
        } catch (ServiceException e) {
            Assertions.assertEquals(401, e.getHTTPCode(), "Login did not return the correct HTTP code.");
        }
        try {
            Assertions.assertNotNull(auths.listAuths(),
                    "Auth list should not be null.");
            Assertions.assertEquals(1, auths.listAuths().size(),
                    "Auth list should have one auth.");
        } catch (DataAccessException e) {
            Assertions.fail("Data Access methods should not throw exceptions. " + e);
        }
    }

    @Test
    public void logoutNormally() {
        LogoutService service = new LogoutService();
        LogoutRequest req = new LogoutRequest(
                existingAuth.authToken());
        try {
            service.logout(req);
        } catch (ServiceException e) {
            Assertions.fail("Logout should not throw exceptions. " + e);
        }
        try {
            Assertions.assertNotNull(auths.listAuths(),
                    "Auth list should not be null.");
            Assertions.assertEquals(0, auths.listAuths().size(),
                    "Auth list should have no auths.");
        } catch (DataAccessException e) {
            Assertions.fail("Data Access methods should not throw exceptions. " + e);
        }
    }

    @Test
    public void logoutWithoutAuth() {
        LogoutService service = new LogoutService();
        LogoutRequest req = new LogoutRequest(
                "incorrect-auth-token");
        try {
            service.logout(req);
            Assertions.fail("Logout should have thrown an exception.");
        } catch (ServiceException e) {
            Assertions.assertEquals(401, e.getHTTPCode(), "Logout did not return the correct HTTP code.");
        }
        try {
            Assertions.assertNotNull(auths.listAuths(),
                    "Auth list should not be null.");
            Assertions.assertEquals(1, auths.listAuths().size(),
                    "Auth list should have one auth.");
        } catch (DataAccessException e) {
            Assertions.fail("Data Access methods should not throw exceptions. " + e);
        }
    }

    @Test
    public void registerNormally() {
        RegisterService service = new RegisterService();
        RegisterRequest req = new RegisterRequest(
                "user2", "pass2", "email2@outlook.com()");
        RegisterResult res = null;
        try {
            res = service.register(req);
        } catch (ServiceException e) {
            Assertions.fail("Register should not throw exceptions. " + e);
        }
        try {
            Assertions.assertNotNull(res,
                    "Result object should not be null.");
            Assertions.assertNotNull(res.authToken(),
                    "Result object had no auth token.");
            Assertions.assertNotNull(res.username(),
                    "Result object had no username.");
            Assertions.assertEquals("user2", res.username(),
                    "Result object did not contain the correct username.");
            Assertions.assertNotNull(users.listUsers(),
                    "User list should not be null.");
            Assertions.assertEquals(2, users.listUsers().size(),
                    "User list should have two users.");
            Assertions.assertNotNull(auths.listAuths(),
                    "Auths list should not be null.");
            Assertions.assertEquals(2, auths.listAuths().size(),
                    "Auths list should contain two auths.");
        } catch (DataAccessException e) {
            Assertions.fail("Data Access methods should not throw exceptions. " + e);
        }
    }

    @Test
    public void registerTakenUsername() {
        RegisterService service = new RegisterService();
        RegisterRequest req = new RegisterRequest(
                existingUser.username(), "pass2", "email2@outlook.com()");
        try {
            service.register(req);
            Assertions.fail("Register should have thrown an exception.");
        } catch (ServiceException e) {
            Assertions.assertEquals(403, e.getHTTPCode(), "Register did not return the correct HTTP code.");
        }
        try {
            Assertions.assertNotNull(users.listUsers(),
                    "User list should not be null.");
            Assertions.assertEquals(1, users.listUsers().size(),
                    "User list should have one user.");
            Assertions.assertNotNull(auths.listAuths(),
                    "Auths list should not be null.");
            Assertions.assertEquals(1, auths.listAuths().size(),
                    "Auths list should contain one auth.");
        } catch (DataAccessException e) {
            Assertions.fail("Data Access methods should not throw exceptions. " + e);
        }
    }
}

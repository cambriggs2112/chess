package dataaccess;

import chess.*;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import model.*;
import java.sql.*;
import java.util.UUID;

public class DataAccessTests {
    private static SQLAuthDAO auth;
    private static SQLGameDAO game;
    private static SQLUserDAO user;
    private static AuthData existingAuth;
    private static GameData existingGame;
    private static UserData existingUser;

    @BeforeEach
    public void setup() {
        try {
            auth = new SQLAuthDAO();
            game = new SQLGameDAO();
            user = new SQLUserDAO();
            existingAuth = new AuthData("9ddacd1c-778b-9a6c-468f-bf7bc268b967", "user");
            existingGame = new GameData(1234, "user", null, "game", new ChessGame());
            existingUser = new UserData("user", "pass", "user@gmail.com");
            clearWithSQL();
            addItemsWithSQL();
        } catch (Exception e) {
            Assertions.fail("Setup should not throw exceptions.");
        }
    }

    @Test
    public void createAuthNormally() {
        try {
            String authToken = UUID.randomUUID().toString();
            AuthData newAuth = new AuthData(authToken, "newUser");
            auth.createAuth(newAuth);
            assertContent("Auth", "AuthToken", authToken, true);
            assertContent("Auth", "Username", "newUser", true);
        } catch (Exception e) {
            Assertions.fail("Create auth should not throw exceptions.");
        }
    }

    @Test
    public void createAuthAlreadyExists() {
        try {
            AuthData newAuth = new AuthData(existingAuth.authToken(), "newUser");
            auth.createAuth(newAuth);
            Assertions.fail("Create auth should have thrown an exception.");
        } catch (DataAccessException e) {
            assertContent("Auth", "Username", "newUser", false);
        }
    }

    @Test
    public void getAuthNormally() {
        try {
            AuthData thisAuth = auth.getAuth(existingAuth.authToken());
            Assertions.assertNotNull(thisAuth, "Auth data returned should not be null.");
            Assertions.assertNotNull(thisAuth.authToken(), "Auth token should not be null.");
            Assertions.assertNotNull(thisAuth.username(), "Username should not be null.");
            Assertions.assertEquals(existingAuth.authToken(), thisAuth.authToken(), "Auth tokens do not match.");
            Assertions.assertEquals(existingAuth.username(), thisAuth.username(), "Usernames do not match.");
        } catch (Exception e) {
            Assertions.fail("Get auth should not throw exceptions.");
        }
    }

    @Test
    public void getAuthNotExists() {
        try {
            AuthData thisAuth = auth.getAuth("unknown-auth-token-1234");
            Assertions.assertNull(thisAuth, "Auth data returned should be null.");
        } catch (Exception e) {
            Assertions.fail("Get auth should not throw exceptions. It should return null objects instead.");
        }
    }

    @Test
    public void getAuthNullToken() {
        AuthData thisAuth = null;
        try {
            thisAuth = auth.getAuth(null);
            Assertions.fail("Get auth should have thrown an exception.");
        } catch (Exception e) {
            Assertions.assertNull(thisAuth, "The object should still be null.");
        }
    }

    @Test
    public void listAuthsNormally() {
        try {
            Assertions.assertNotNull(auth.listAuths(), "List object should not be null.");
            Assertions.assertEquals(1, auth.listAuths().size(), "List object should be of size 1.");
            Assertions.assertNotNull(auth.listAuths().get(0), "Object in list should not be null.");
            Assertions.assertNotNull(auth.listAuths().get(0).authToken(), "Auth token in list should not be null.");
            Assertions.assertNotNull(auth.listAuths().get(0).username(), "Username in list should not be null.");
            Assertions.assertEquals(existingAuth.authToken(), auth.listAuths().get(0).authToken(), "Auth tokens do not match.");
            Assertions.assertEquals(existingAuth.username(), auth.listAuths().get(0).username(), "Usernames do not match.");
            addItemsWithSQLRandom();
            Assertions.assertNotNull(auth.listAuths(), "List object should not be null.");
            Assertions.assertEquals(2, auth.listAuths().size(), "List object should be of size 2.");
        } catch (Exception e) {
            Assertions.fail("List auths should not throw exceptions.");
        }
    }

    @Test
    public void listAuthsEmpty() {
        try {
            clearWithSQL();
            Assertions.assertNotNull(auth.listAuths(), "List object should not be null.");
            Assertions.assertEquals(0, auth.listAuths().size(), "List object should be of size 0.");
        } catch (Exception e) {
            Assertions.fail("List auths should not throw exceptions.");
        }
    }

    @Test
    public void deleteAuthNormally() {
        try {
            auth.deleteAuth(existingAuth.authToken());
            assertContent("Auth", "AuthToken", existingAuth.authToken(), false);
            assertContent("Auth", "Username", existingAuth.username(), false);
        } catch (Exception e) {
            Assertions.fail("Delete auth should not throw exceptions.");
        }
    }

    @Test
    public void deleteAuthNotExists() {
        try {
            auth.deleteAuth("unknown-auth-token-1234");
            Assertions.fail("Delete auth should have thrown an exception.");
        } catch (DataAccessException e) {
            assertContent("Auth", "AuthToken", existingAuth.authToken(), true);
            assertContent("Auth", "Username", existingAuth.username(), true);
        }
    }

    @Test
    public void deleteAuthNullToken() {
        try {
            auth.deleteAuth(null);
            Assertions.fail("Delete auth should have thrown an exception.");
        } catch (DataAccessException e) {
            assertContent("Auth", "AuthToken", existingAuth.authToken(), true);
            assertContent("Auth", "Username", existingAuth.username(), true);
        }
    }

    @Test
    public void clearAuthsNormally() {
        try {
            auth.clear();
            assertContent("Auth", "AuthToken", existingAuth.authToken(), false);
            assertContent("Auth", "Username", existingAuth.username(), false);
        } catch (DataAccessException e) {
            Assertions.fail("Clear auths should not throw exceptions.");
        }
    }

    @Test
    public void createGameNormally() {
        try {
            int gameID = Math.abs(UUID.randomUUID().hashCode());
            GameData newGame = new GameData(gameID, null, null, "newGame", new ChessGame());
            game.createGame(newGame);
            assertContent("Game", "GameID", gameID, true);
            assertContent("Game", "GameName", "newGame", true);
        } catch (Exception e) {
            Assertions.fail("Create game should not throw exceptions.");
        }
    }

    @Test
    public void createGameAlreadyExists() {
        try {
            GameData newGame = new GameData(existingGame.gameID(), null, null, "newGame", new ChessGame());
            game.createGame(newGame);
            Assertions.fail("Create game should have thrown an exception.");
        } catch (DataAccessException e) {
            assertContent("Game", "GameName", "newGame", false);
        }
    }

    @Test
    public void getGameNormally() {
        try {
            GameData thisGame = game.getGame(existingGame.gameID());
            Assertions.assertNotNull(thisGame, "Game data returned should not be null.");
            Assertions.assertNotNull(thisGame.gameID(), "Game ID should not be null.");
            Assertions.assertNotNull(thisGame.whiteUsername(), "White username should not be null.");
            Assertions.assertNull(thisGame.blackUsername(), "Black username should be null.");
            Assertions.assertNotNull(thisGame.gameName(), "Game name should not be null.");
            Assertions.assertNotNull(thisGame.game(), "Game should not be null.");
            Assertions.assertEquals(existingGame.gameID(), thisGame.gameID(), "Game IDs do not match.");
            Assertions.assertEquals(existingGame.whiteUsername(), thisGame.whiteUsername(), "White usernames do not match.");
            Assertions.assertEquals(existingGame.gameName(), thisGame.gameName(), "Game names do not match.");
        } catch (Exception e) {
            Assertions.fail("Get game should not throw exceptions.");
        }
    }

    @Test
    public void getGameNotExists() {
        try {
            GameData thisGame = game.getGame(5678);
            Assertions.assertNull(thisGame, "Game data returned should be null.");
        } catch (Exception e) {
            Assertions.fail("Get game should not throw exceptions. It should return null objects instead.");
        }
    }

    @Test
    public void getGameNullID() {
        GameData thisGame = null;
        try {
            thisGame = game.getGame(null);
            Assertions.fail("Get game should have thrown an exception.");
        } catch (Exception e) {
            Assertions.assertNull(thisGame, "The object should still be null.");
        }
    }

    @Test
    public void listGamesNormally() {
        try {
            Assertions.assertNotNull(game.listGames(), "List object should not be null.");
            Assertions.assertEquals(1, game.listGames().size(), "List object should be of size 1.");
            Assertions.assertNotNull(game.listGames().get(0), "Object in list should not be null.");
            Assertions.assertNotNull(game.listGames().get(0).gameID(), "Game ID in list should not be null.");
            Assertions.assertNotNull(game.listGames().get(0).whiteUsername(), "White username in list should not be null.");
            Assertions.assertNull(game.listGames().get(0).blackUsername(), "Black username in list should be null.");
            Assertions.assertNotNull(game.listGames().get(0).gameName(), "Game name in list should not be null.");
            Assertions.assertEquals(existingGame.gameID(), game.listGames().get(0).gameID(), "Game IDs do not match.");
            Assertions.assertEquals(existingGame.whiteUsername(), game.listGames().get(0).whiteUsername(), "White usernames do not match.");
            Assertions.assertEquals(existingGame.gameName(), game.listGames().get(0).gameName(), "Game names do not match.");
            addItemsWithSQLRandom();
            Assertions.assertNotNull(game.listGames(), "List object should not be null.");
            Assertions.assertEquals(2, game.listGames().size(), "List object should be of size 2.");
        } catch (Exception e) {
            Assertions.fail("List games should not throw exceptions.");
        }
    }

    @Test
    public void listGamesEmpty() {
        try {
            clearWithSQL();
            Assertions.assertNotNull(game.listGames(), "List object should not be null.");
            Assertions.assertEquals(0, game.listGames().size(), "List object should be of size 0.");
        } catch (Exception e) {
            Assertions.fail("List games should not throw exceptions.");
        }
    }

    @Test
    public void updateGameNormally() {
        try {
            ChessGame gameObj = existingGame.game();
            // Move a pawn
            gameObj.makeMove(new ChessMove(new ChessPosition(2, 1), new ChessPosition(3, 1), null));
            // Join black user and apply move
            game.updateGame(new GameData(existingGame.gameID(), existingGame.whiteUsername(), "user2", existingGame.gameName(), gameObj));
            assertContent("Game", "BlackUsername", "user2", true);
        } catch (Exception e) {
            Assertions.fail("Update game should not throw exceptions.");
        }
    }

    @Test
    public void updateGameNotExists() {
        try {
            ChessGame gameObj = existingGame.game();
            // Move a pawn
            gameObj.makeMove(new ChessMove(new ChessPosition(2, 1), new ChessPosition(3, 1), null));
            // Join black user and apply move, but alter game ID
            game.updateGame(new GameData(5678, existingGame.whiteUsername(), "user2", existingGame.gameName(), gameObj));
            Assertions.fail("Update game should have thrown an exception.");
        } catch (Exception e) {
            assertContent("Game", "GameID", existingGame.gameID(), true);
            assertContent("Game", "WhiteUsername", existingGame.whiteUsername(), true);
            assertContent("Game", "GameName", existingGame.gameName(), true);
        }
    }

    @Test
    public void clearGamesNormally() {
        try {
            game.clear();
            assertContent("Game", "GameID", existingGame.gameID(), false);
            assertContent("Game", "WhiteUsername", existingGame.whiteUsername(), false);
            assertContent("Game", "GameName", existingGame.gameName(), false);
        } catch (DataAccessException e) {
            Assertions.fail("Clear games should not throw exceptions.");
        }
    }

    @Test
    public void createUserNormally() {
        try {
            UserData newUser = new UserData("newUser", "newPass", "newuser@outlook.com");
            user.createUser(newUser);
            assertContent("User", "Username", "newUser", true);
            assertContent("User", "Password", "newPass", true);
            assertContent("User", "Email", "newuser@outlook.com", true);
        } catch (Exception e) {
            Assertions.fail("Create user should not throw exceptions.");
        } // NOTE: Password hashing is done in the service classes that call the DAO.
    }

    @Test
    public void createUserAlreadyExists() {
        try {
            UserData newUser = new UserData(existingUser.username(), "newPass", "newuser@outlook.com");
            user.createUser(newUser);
            Assertions.fail("Create user should have thrown an exception.");
        } catch (DataAccessException e) {
            assertContent("User", "Password", "newPass", false);
            assertContent("User", "Email", "newuser@outlook.com", false);
        } // NOTE: Password hashing is done in the service classes that call the DAO.
    }

    @Test
    public void getUserNormally() {
        try {
            UserData thisUser = user.getUser(existingUser.username());
            Assertions.assertNotNull(thisUser, "User data returned should not be null.");
            Assertions.assertNotNull(thisUser.username(), "Username should not be null.");
            Assertions.assertNotNull(thisUser.password(), "Password should not be null.");
            Assertions.assertNotNull(thisUser.email(), "Email should not be null.");
            Assertions.assertEquals(existingUser.username(), thisUser.username(), "Usernames do not match.");
            Assertions.assertEquals(existingUser.password(), thisUser.password(), "Passwords do not match.");
            Assertions.assertEquals(existingUser.email(), thisUser.email(), "Emails do not match.");
        } catch (Exception e) {
            Assertions.fail("Get user should not throw exceptions.");
        } // NOTE: Password hashing is done in the service classes that call the DAO.
    }

    @Test
    public void getUserNotExists() {
        try {
            UserData thisUser = user.getUser("unknownUser");
            Assertions.assertNull(thisUser, "User data returned should be null.");
        } catch (Exception e) {
            Assertions.fail("Get user should not throw exceptions. It should return null objects instead.");
        }
    }

    @Test
    public void getUserNullName() {
        UserData thisUser = null;
        try {
            thisUser = user.getUser(null);
            Assertions.fail("Get user should have thrown an exception.");
        } catch (Exception e) {
            Assertions.assertNull(thisUser, "The object should still be null.");
        }
    }

    @Test
    public void listUsersNormally() {
        try {
            Assertions.assertNotNull(user.listUsers(), "List object should not be null.");
            Assertions.assertEquals(1, user.listUsers().size(), "List object should be of size 1.");
            Assertions.assertNotNull(user.listUsers().get(0), "Object in list should not be null.");
            Assertions.assertNotNull(user.listUsers().get(0).username(), "Username in list should not be null.");
            Assertions.assertNotNull(user.listUsers().get(0).password(), "Password in list should not be null.");
            Assertions.assertNotNull(user.listUsers().get(0).email(), "Email in list should not be null.");
            Assertions.assertEquals(existingUser.username(), user.listUsers().get(0).username(), "Usernames do not match.");
            Assertions.assertEquals(existingUser.password(), user.listUsers().get(0).password(), "Passwords do not match.");
            Assertions.assertEquals(existingUser.email(), user.listUsers().get(0).email(), "Emails do not match.");
            addItemsWithSQLRandom();
            Assertions.assertNotNull(user.listUsers(), "List object should not be null.");
            Assertions.assertEquals(2, user.listUsers().size(), "List object should be of size 2.");
        } catch (Exception e) {
            Assertions.fail("List auths should not throw exceptions.");
        }
    }

    @Test
    public void listUsersEmpty() {
        try {
            clearWithSQL();
            Assertions.assertNotNull(user.listUsers(), "List object should not be null.");
            Assertions.assertEquals(0, user.listUsers().size(), "List object should be of size 0.");
        } catch (Exception e) {
            Assertions.fail("List users should not throw exceptions.");
        }
    }

    @Test
    public void clearUsersNormally() {
        try {
            user.clear();
            assertContent("User", "Username", existingUser.username(), false);
            assertContent("User", "Password", existingUser.password(), false);
            assertContent("User", "Email", existingUser.email(), false);
        } catch (DataAccessException e) {
            Assertions.fail("Clear users should not throw exceptions.");
        }
    }

    public static String serialize(ChessGame game) {
        Gson gson = new Gson();
        return gson.toJson(game);
    }

    public static void assertContent(String table, String col, String item, boolean isPositiveCheck) {
        try (Connection conn = DatabaseManager.getConnection()) {
            ResultSet res = conn.prepareStatement(String.format(
                    "SELECT * FROM %s WHERE %s='%s';", table, col, item)).executeQuery();
            if (isPositiveCheck) {
                Assertions.assertTrue(res.next(), String.format(
                        "The column %s in the table %s did not contain %s.", col, table, item));
            } else {
                Assertions.assertFalse(res.next(), String.format(
                        "The column %s in the table %s contained %s.", col, table, item));
            }
        } catch (Exception e) {
            Assertions.fail("Checks should not throw exceptions.");
        }
    }

    public static void assertContent(String table, String col, int item, boolean isPositiveCheck) {
        try (Connection conn = DatabaseManager.getConnection()) {
            ResultSet res = conn.prepareStatement(String.format(
                    "SELECT * FROM %s WHERE %s=%d;", table, col, item)).executeQuery();
            if (isPositiveCheck) {
                Assertions.assertTrue(res.next(), String.format(
                        "The column %s in the table %s did not contain %d.", col, table, item));
            } else {
                Assertions.assertFalse(res.next(), String.format(
                        "The column %s in the table %s contained %d.", col, table, item));
            }
        } catch (Exception e) {
            Assertions.fail("Checks should not throw exceptions.");
        }
    }

    public static void clearWithSQL() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.prepareStatement("TRUNCATE Auth;").executeUpdate();
            conn.prepareStatement("TRUNCATE Game;").executeUpdate();
            conn.prepareStatement("TRUNCATE User;").executeUpdate();
        } catch (Exception e) {
            throw new DataAccessException("Exception: " + e);
        }
    }

    public static void addItemsWithSQL() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.prepareStatement(
                    String.format("INSERT INTO Auth (AuthToken, Username) VALUES ('%s', '%s');",
                            existingAuth.authToken(), existingAuth.username())
            ).executeUpdate();
            conn.prepareStatement(
                    String.format("INSERT INTO Game (GameID, WhiteUsername, GameName, GameObject) VALUES ('%d', '%s', '%s', '%s');",
                            existingGame.gameID(), existingGame.whiteUsername(), existingGame.gameName(), serialize(existingGame.game()))
            ).executeUpdate();
            conn.prepareStatement(
                    String.format("INSERT INTO User (Username, Password, Email) VALUES ('%s', '%s', '%s');",
                            existingUser.username(), existingUser.password(), existingUser.email())
            ).executeUpdate();
        } catch (Exception e) {
            throw new DataAccessException("Exception: " + e);
        }
    }

    public static void addItemsWithSQLRandom() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.prepareStatement(
                    String.format("INSERT INTO Auth (AuthToken, Username) VALUES ('%s', '%s');",
                            UUID.randomUUID(), UUID.randomUUID())
            ).executeUpdate();
            conn.prepareStatement(
                    String.format("INSERT INTO Game (GameID, GameName, GameObject) VALUES ('%d', '%s', '%s');",
                            Math.abs(UUID.randomUUID().hashCode()), UUID.randomUUID(), serialize(new ChessGame()))
            ).executeUpdate();
            conn.prepareStatement(
                    String.format("INSERT INTO User (Username, Password, Email) VALUES ('%s', '%s', '%s');",
                            UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID())
            ).executeUpdate();
        } catch (Exception e) {
            throw new DataAccessException("Exception: " + e);
        }
    }
}

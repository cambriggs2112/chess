package dataaccess;

import chess.ChessGame;
import org.junit.jupiter.api.*;
import model.*;

public class DataAccessTests {
    @Test
    public void sandbox() {
        try {
            SQLAuthDAO auth = new SQLAuthDAO();
            SQLGameDAO game = new SQLGameDAO();
            auth.clear();
            game.clear();
            auth.createAuth(new AuthData("testToken", "testName"));
            System.out.println(auth.getAuth("testToken").authToken());
            System.out.println(auth.getAuth("testToken").username());
            System.out.println(auth.listAuths().size());
            auth.deleteAuth("testToken");
            System.out.println(auth.listAuths().size());
            auth.clear();
            game.createGame(new GameData(1234, null, null, "testName", new ChessGame()));
            System.out.println(game.getGame(1234).gameID());
            if (game.getGame(1234).whiteUsername() == null) {
                System.out.println("null");
            }
            if (game.getGame(1234).blackUsername() == null) {
                System.out.println("null");
            }
            System.out.println(game.getGame(1234).gameName());
            if (game.getGame(1234).game() == null) {
                System.out.println("null");
            }
            System.out.println(game.listGames().size());
            game.updateGame(new GameData(1234, "testWhiteName", "testBlackName", "testName", new ChessGame()));
            System.out.println(game.listGames().size());
            System.out.println(game.getGame(1234).gameID());
            System.out.println(game.getGame(1234).whiteUsername());
            System.out.println(game.getGame(1234).blackUsername());
            System.out.println(game.getGame(1234).gameName());
            game.clear();
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
        Assertions.assertTrue(true);
    }
}

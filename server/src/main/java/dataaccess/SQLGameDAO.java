package dataaccess;

import com.google.gson.Gson;
import model.GameData;
import chess.ChessGame;
import java.sql.*;
import java.util.ArrayList;

/**
 * An SQL-based database of game data (gameID, whiteUsername, blackUsername, gameName, game).
 */
public class SQLGameDAO implements GameDAO {
    public SQLGameDAO() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.prepareStatement("""
                    CREATE TABLE IF NOT EXISTS Game (
                      GameID int PRIMARY KEY,
                      WhiteUsername varchar(128),
                      BlackUsername varchar(128),
                      GameName varchar(128) NOT NULL,
                      GameObject varchar(2048) NOT NULL
                    );
                    """).executeUpdate(); // GameObject is JSON
        } catch (SQLException e) {
            throw new DataAccessException("Unable to create game table: " + e);
        }
    }

    /**
     * Adds an object of game data to the database
     *
     * @param newGame the game data object to add
     * @throws DataAccessException if game ID already exists
     */
    public void createGame(GameData newGame) throws DataAccessException {
        Gson gson = new Gson();
        try (Connection conn = DatabaseManager.getConnection()) {
            if (newGame.whiteUsername() == null) {
                if (newGame.blackUsername() == null) {
                    conn.prepareStatement(String.format(
                            "INSERT INTO Game (GameID, GameName, GameObject) VALUES ('%d', '%s', '%s');",
                            newGame.gameID(),
                            newGame.gameName(),
                            gson.toJson(newGame.game()))
                    ).executeUpdate();
                } else {
                    conn.prepareStatement(String.format(
                            "INSERT INTO Game (GameID, BlackUsername, GameName, GameObject) VALUES ('%d', '%s', '%s', '%s');",
                            newGame.gameID(),
                            newGame.blackUsername(),
                            newGame.gameName(),
                            gson.toJson(newGame.game()))
                    ).executeUpdate();
                }
            } else {
                if (newGame.blackUsername() == null) {
                    conn.prepareStatement(String.format(
                            "INSERT INTO Game (GameID, WhiteUsername, GameName, GameObject) VALUES ('%d', '%s', '%s', '%s');",
                            newGame.gameID(),
                            newGame.whiteUsername(),
                            newGame.gameName(),
                            gson.toJson(newGame.game()))
                    ).executeUpdate();
                } else {
                    conn.prepareStatement(String.format(
                            "INSERT INTO Game (GameID, WhiteUsername, BlackUsername, GameName, GameObject) VALUES ('%d', '%s', '%s', '%s', '%s');",
                            newGame.gameID(),
                            newGame.whiteUsername(),
                            newGame.blackUsername(),
                            newGame.gameName(),
                            gson.toJson(newGame.game()))
                    ).executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to create game data: " + e);
        }
    }

    /**
     * @return object of game data by game ID or null if game ID does not exist
     *
     * @param gameID the gameID of the requested data
     */
    public GameData getGame(Integer gameID) throws DataAccessException {
        Gson gson = new Gson();
        try (Connection conn = DatabaseManager.getConnection()) {
            ResultSet results = conn.prepareStatement(
                    String.format("SELECT * FROM Game WHERE GameID=%d;", gameID)
            ).executeQuery();
            if (results.next()) { // detect if an object is in the set
                return new GameData(
                        gameID,
                        results.getString("WhiteUsername"),
                        results.getString("BlackUsername"),
                        results.getString("GameName"),
                        gson.fromJson(results.getString("GameObject"), ChessGame.class));
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to get game data: " + e);
        }
    }

    /**
     * @return ArrayList of all game data objects in the database
     */
    public ArrayList<GameData> listGames() throws DataAccessException {
        ArrayList<GameData> resultList = new ArrayList<GameData>();
        Gson gson = new Gson();
        try (Connection conn = DatabaseManager.getConnection()) {
            ResultSet results = conn.prepareStatement("SELECT * FROM Game;").executeQuery();
            while (results.next()) {
                resultList.add(new GameData(
                        results.getInt("GameID"),
                        results.getString("WhiteUsername"),
                        results.getString("BlackUsername"),
                        results.getString("GameName"),
                        gson.fromJson(results.getString("GameObject"), ChessGame.class)));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to list game data: " + e);
        }
        return resultList;
    }

    /**
     * Replaces an object of game data in the database by game ID
     *
     * @param newGame the new game data object to replace the existing one
     * @throws DataAccessException if game ID does not exist
     */
    public void updateGame(GameData newGame) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.prepareStatement(
                    String.format("DELETE FROM Game WHERE GameID=%d;", newGame.gameID())
            ).executeUpdate();
            createGame(newGame);
        } catch (SQLException e) {
            throw new DataAccessException("Unable to update game data: " + e);
        }
    }

    /**
     * Deletes all game data in the database
     */
    public void clear() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.prepareStatement("TRUNCATE Game;").executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Unable to clear game table: " + e);
        }
    }
}

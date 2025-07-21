package dataaccess;

import model.GameData;
import java.util.ArrayList;

/**
 * An SQL-based database of game data (gameID, whiteUsername, blackUsername, gameName, game).
 */
public class SQLGameDAO implements GameDAO {
    public SQLGameDAO() {

    }

    /**
     * Adds an object of game data to the database
     *
     * @param newGame the game data object to add
     * @throws DataAccessException if game ID is null or already exists
     */
    public void createGame(GameData newGame) throws DataAccessException {

    }

    /**
     * @return object of game data by game ID or null if game ID does not exist
     *
     * @param gameID the gameID of the requested data
     * @throws DataAccessException if game ID is null
     */
    public GameData getGame(Integer gameID) throws DataAccessException {
        return null;
    }

    /**
     * @return ArrayList of all game data objects in the database
     */
    public ArrayList<GameData> listGames() throws DataAccessException {
        return null;
    }

    /**
     * Replaces an object of game data in the database by game ID
     *
     * @param newGame the new game data object to replace the existing one
     * @throws DataAccessException if game ID is null or does not exist
     */
    public void updateGame(GameData newGame) throws DataAccessException {

    }

    /**
     * Deletes all game data in the database
     */
    public void clear() throws DataAccessException {

    }
}

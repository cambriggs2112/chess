package dataaccess;

import model.GameData;
import java.util.ArrayList;

/**
 * A memory-based database of game data (gameID, whiteUsername, blackUsername, gameName,
 * game) that uses an ArrayList.
 */
public class MemoryGameDAO implements GameDAO {
    private static ArrayList<GameData> gameDatabase = new ArrayList<GameData>();

    public MemoryGameDAO() {}

    /**
     * Adds an object of game data to the database
     *
     * @param newGame the game data object to add
     * @throws DataAccessException if game ID is null or already exists
     */
    public void createGame(GameData newGame) throws DataAccessException {
        if (getGame(newGame.gameID()) != null) {
            throw new DataAccessException("Unable to create game data: Game ID already exists.");
        }
        gameDatabase.add(newGame);
    }

    /**
     * @return object of game data by game ID or null if game ID does not exist
     *
     * @param gameID the gameID of the requested data
     * @throws DataAccessException if game ID is null
     */
    public GameData getGame(Integer gameID) throws DataAccessException {
        if (gameID == null) {
            throw new DataAccessException("Unable to get game data: Game ID cannot be null.");
        }
        for (GameData g : gameDatabase) {
            if (g.gameID().equals(gameID)) {
                return g;
            }
        }
        return null;
    }

    /**
     * @return ArrayList of all game data objects in the database
     */
    public ArrayList<GameData> listGames() throws DataAccessException {
        return gameDatabase;
    }

    /**
     * Replaces an object of game data in the database by game ID
     *
     * @param newGame the new game data object to replace the existing one
     * @throws DataAccessException if game ID is null or does not exist
     */
    public void updateGame(GameData newGame) throws DataAccessException {
        GameData g = getGame(newGame.gameID());
        if (g == null) {
            throw new DataAccessException("Unable to update game data: Game ID not found.");
        }
        int index = gameDatabase.indexOf(g);
        gameDatabase.set(index, newGame);
    }

    /**
     * Deletes all game data in the database
     */
    public void clear() throws DataAccessException {
        gameDatabase.clear();
    }
}

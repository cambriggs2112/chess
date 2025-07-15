package dataaccess;

import model.GameData;
import java.util.ArrayList;

public class MemoryGameDAO implements GameDAO {
    private ArrayList<GameData> gameDatabase;

    public MemoryGameDAO() {
        this.gameDatabase = new ArrayList<GameData>();
    }

    public void createGame(GameData newGame) throws DataAccessException {
        if (getGame(newGame.gameID()) != null) {
            throw new DataAccessException("Unable to create game data: Game ID already exists.");
        }
        gameDatabase.add(newGame);
    }

    public GameData getGame(int gameID) throws DataAccessException {
        for (GameData g : gameDatabase) {
            if (g.gameID() == gameID) {
                return g;
            }
        }
        return null;
    }

    public ArrayList<GameData> listGames() throws DataAccessException {
        return gameDatabase;
    }

    public void updateGame(GameData newGame) throws DataAccessException {
        GameData g = getGame(newGame.gameID());
        if (g == null) {
            throw new DataAccessException("Unable to update game data: Game ID not found.");
        }
        int index = gameDatabase.indexOf(g);
        gameDatabase.set(index, newGame);
    }

    public void deleteGame(int gameID) throws DataAccessException {
        GameData g = getGame(gameID);
        if (g == null) {
            throw new DataAccessException("Unable to delete game data: Game ID not found.");
        }
        gameDatabase.remove(g);
    }

    public void clear() throws DataAccessException {
        gameDatabase.clear();
    }
}

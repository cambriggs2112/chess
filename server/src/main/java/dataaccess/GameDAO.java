package dataaccess;

import java.util.ArrayList;

public class GameDAO {
    private ArrayList<GameData> gameDatabase;

    public GameDAO() {
        this.gameDatabase = new ArrayList<GameData>();
    }

    public void createGame(GameData newGame) throws DataAccessException {
        if (getGame(newGame.getGameID()) != null) {
            throw new DataAccessException("gameID already exists for createGame()");
        }
        gameDatabase.add(newGame);
    }

    public GameData getGame(int gameID) {
        for (GameData g : gameDatabase) {
            if (g.getGameID() == gameID) {
                return g;
            }
        }
        return null;
    }

    public ArrayList<GameData> listGames() {
        return gameDatabase;
    }

    public void updateGame(int gameID) throws DataAccessException {
        GameData g = getGame(gameID);
        if (g == null) {
            throw new DataAccessException("gameID not found for updateGame()");
        }
        int index = gameDatabase.indexOf(g);
        gameDatabase.set(index, g);
    }

    public void deleteGame(int gameID) throws DataAccessException {
        GameData g = getGame(gameID);
        if (g == null) {
            throw new DataAccessException("gameID not found for deleteGame()");
        }
        gameDatabase.remove(g);
    }

    public void clear() {
        gameDatabase.clear();
    }
}

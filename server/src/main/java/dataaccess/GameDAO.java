package dataaccess;

import java.util.ArrayList;
import model.GameData;

/**
 * Generic game database template
 */
public interface GameDAO {
    void createGame(GameData newGame) throws DataAccessException;
    GameData getGame(Integer gameID) throws DataAccessException;
    ArrayList<GameData> listGames() throws DataAccessException;
    void updateGame(GameData newGame) throws DataAccessException;
    void clear() throws DataAccessException;
}

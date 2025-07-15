package dataaccess;

import java.util.ArrayList;
import model.GameData;

public interface GameDAO {
    void createGame(GameData newAuth) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    ArrayList<GameData> listGames() throws DataAccessException;
    void updateGame(int gameID) throws DataAccessException;
    void deleteGame(int gameID) throws DataAccessException;
    void clear() throws DataAccessException;
}

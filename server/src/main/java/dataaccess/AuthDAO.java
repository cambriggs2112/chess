package dataaccess;

import java.util.ArrayList;
import model.AuthData;

/**
 * Generic authorization database template
 */
public interface AuthDAO {
    void createAuth(AuthData newAuth) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    ArrayList<AuthData> listAuths() throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
    void clear() throws DataAccessException;
}

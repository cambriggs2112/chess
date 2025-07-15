package dataaccess;

import java.util.ArrayList;
import model.AuthData;

public interface AuthDAO {
    void createAuth(AuthData newAuth) throws DataAccessException;
    AuthData getAuth(String authToken);
    ArrayList<AuthData> listAuths();
    void updateAuth(String authToken) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
    void clear();
}

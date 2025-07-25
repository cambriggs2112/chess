package dataaccess;

import java.util.ArrayList;
import model.UserData;

/**
 * Generic user database template
 */
public interface UserDAO {
    void createUser(UserData newUser) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    ArrayList<UserData> listUsers() throws DataAccessException;
    void clear() throws DataAccessException;
}

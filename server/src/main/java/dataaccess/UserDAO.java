package dataaccess;

import java.util.ArrayList;
import model.UserData;

public interface UserDAO {
    void createUser(UserData newAuth) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    ArrayList<UserData> listUsers() throws DataAccessException;
    void updateUser(String username) throws DataAccessException;
    void deleteUser(String username) throws DataAccessException;
    void clear() throws DataAccessException;
}

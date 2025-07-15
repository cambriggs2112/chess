package dataaccess;

import java.util.ArrayList;
import model.UserData;

public interface UserDAO {
    void createUser(UserData newAuth) throws DataAccessException;
    UserData getUser(String username);
    ArrayList<UserData> listUsers();
    void updateUser(String username) throws DataAccessException;
    void deleteUser(String username) throws DataAccessException;
    void clear();
}

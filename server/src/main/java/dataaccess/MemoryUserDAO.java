package dataaccess;

import model.UserData;

import java.util.ArrayList;

public class MemoryUserDAO implements UserDAO {
    private ArrayList<UserData> userDatabase;

    public MemoryUserDAO() {
        this.userDatabase = new ArrayList<UserData>();
    }

    public void createUser(UserData newUser) throws DataAccessException {
        if (getUser(newUser.username()) != null) {
            throw new DataAccessException("username already exists for createUser()");
        }
        userDatabase.add(newUser);
    }

    public UserData getUser(String username) throws DataAccessException {
        for (UserData u : userDatabase) {
            if (u.username().equals(username)) {
                return u;
            }
        }
        return null;
    }

    public ArrayList<UserData> listUsers() throws DataAccessException {
        return userDatabase;
    }

    public void updateUser(String username) throws DataAccessException {
        UserData u = getUser(username);
        if (u == null) {
            throw new DataAccessException("username not found for updateUser()");
        }
        int index = userDatabase.indexOf(u);
        userDatabase.set(index, u);
    }

    public void deleteUser(String username) throws DataAccessException {
        UserData u = getUser(username);
        if (u == null) {
            throw new DataAccessException("username not found for deleteUser()");
        }
        userDatabase.remove(u);
    }

    public void clear() throws DataAccessException {
        userDatabase.clear();
    }
}

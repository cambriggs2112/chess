package dataaccess;

import java.util.ArrayList;

public class UserDAO {
    private ArrayList<UserData> userDatabase;

    public UserDAO() {
        this.userDatabase = new ArrayList<UserData>();
    }

    public void createUser(UserData newUser) throws DataAccessException {
        if (getUser(newUser.getUsername()) != null) {
            throw new DataAccessException("username already exists for createUser()");
        }
        userDatabase.add(newUser);
    }

    public UserData getUser(String username) {
        for (UserData u : userDatabase) {
            if (u.getUsername().equals(username)) {
                return u;
            }
        }
        return null;
    }

    public ArrayList<UserData> listUsers() {
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

    public void clear() {
        userDatabase.clear();
    }
}

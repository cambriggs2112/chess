package dataaccess;

import model.UserData;

import java.util.ArrayList;

/**
 * A memory-based database of user data (username, password, email) that uses an ArrayList.
 */
public class MemoryUserDAO implements UserDAO {
    private ArrayList<UserData> userDatabase;

    public MemoryUserDAO() {
        this.userDatabase = new ArrayList<UserData>();
    }

    /**
     * Adds an object of user data to the database
     *
     * @param newUser the user data object to add
     * @throws DataAccessException if username is null or already exists
     */
    public void createUser(UserData newUser) throws DataAccessException {
        if (getUser(newUser.username()) != null) {
            throw new DataAccessException("Unable to create user data: Username already exists.");
        }
        userDatabase.add(newUser);
    }

    /**
     * @return object of user data by username or null if username does not exist
     *
     * @param username the username of the requested data
     * @throws DataAccessException if username is null
     */
    public UserData getUser(String username) throws DataAccessException {
        for (UserData u : userDatabase) {
            if (username == null) {
                throw new DataAccessException("Unable to get user data: Username cannot be null.");
            }
            if (u.username().equals(username)) {
                return u;
            }
        }
        return null;
    }

    /**
     * @return ArrayList of all user data objects in the database
     */
    public ArrayList<UserData> listUsers() throws DataAccessException {
        return userDatabase;
    }

    /**
     * Replaces an object of user data in the database by username
     *
     * @param newUser the new user data object to replace the existing one
     * @throws DataAccessException if username is null or does not exist
     */
    public void updateUser(UserData newUser) throws DataAccessException {
        UserData u = getUser(newUser.username());
        if (u == null) {
            throw new DataAccessException("Unable to update user data: Username not found.");
        }
        int index = userDatabase.indexOf(u);
        userDatabase.set(index, newUser);
    }

    /**
     * Deletes an object of user data by username
     *
     * @param username the username of the object to delete
     * @throws DataAccessException if username is null or does not exist
     */
    public void deleteUser(String username) throws DataAccessException {
        UserData u = getUser(username);
        if (u == null) {
            throw new DataAccessException("Unable to delete user data: Username not found.");
        }
        userDatabase.remove(u);
    }

    /**
     * Deletes all user data in the database
     */
    public void clear() throws DataAccessException {
        userDatabase.clear();
    }
}

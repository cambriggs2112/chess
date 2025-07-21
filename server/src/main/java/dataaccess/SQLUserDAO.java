package dataaccess;

import model.UserData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * An SQL-based database of user data (username, password, email).
 */
public class SQLUserDAO implements UserDAO {
    public SQLUserDAO() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.prepareStatement("""
                    CREATE TABLE IF NOT EXISTS User (
                      Username varchar(128) PRIMARY KEY,
                      Password varchar(64) NOT NULL,
                      Email varchar(256) NOT NULL
                    );
                    """).executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Unable to create user table: " + e);
        }
    }

    /**
     * Adds an object of user data to the database
     *
     * @param newUser the user data object to add
     * @throws DataAccessException if username is null or already exists
     */
    public void createUser(UserData newUser) throws DataAccessException {

    }

    /**
     * @return object of user data by username or null if username does not exist
     *
     * @param username the username of the requested data
     * @throws DataAccessException if username is null
     */
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    /**
     * @return ArrayList of all user data objects in the database
     */
    public ArrayList<UserData> listUsers() throws DataAccessException {
        return null;
    }

    /**
     * Deletes all user data in the database
     */
    public void clear() throws DataAccessException {

    }
}

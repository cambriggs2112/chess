package dataaccess;

import model.UserData;
import java.sql.Connection;
import java.sql.ResultSet;
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
                    """).executeUpdate(); // Password is hashed in code that uses this DAO
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
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.prepareStatement(
                    String.format("INSERT INTO User (Username, Password, Email) VALUES ('%s', '%s', '%s');",
                            newUser.username(), newUser.password(), newUser.email())
            ).executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Unable to create user data: " + e);
        }
    }

    /**
     * @return object of user data by username or null if username does not exist
     *
     * @param username the username of the requested data
     * @throws DataAccessException if username is null
     */
    public UserData getUser(String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            ResultSet results = conn.prepareStatement(
                    String.format("SELECT * FROM User WHERE Username='%s';", username)
            ).executeQuery();
            if (results.next()) { // detect if an object is in the set
                return new UserData(username, results.getString("Password"), results.getString("Email"));
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to get user data: " + e);
        }
    }

    /**
     * @return ArrayList of all user data objects in the database
     */
    public ArrayList<UserData> listUsers() throws DataAccessException {
        ArrayList<UserData> resultList = new ArrayList<UserData>();
        try (Connection conn = DatabaseManager.getConnection()) {
            ResultSet results = conn.prepareStatement("SELECT * FROM User;").executeQuery();
            while (results.next()) {
                resultList.add(new UserData(
                        results.getString("Username"),
                        results.getString("Password"),
                        results.getString("Email")));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to list user data: " + e);
        }
        return resultList;
    }

    /**
     * Deletes all user data in the database
     */
    public void clear() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.prepareStatement("TRUNCATE User;").executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Unable to clear user table: " + e);
        }
    }
}

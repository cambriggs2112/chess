package dataaccess;

import model.AuthData;
import java.util.ArrayList;
import java.sql.*;

/**
 * An SQL-based database of authorization data (authToken, username).
 */
public class SQLAuthDAO implements AuthDAO {
    public SQLAuthDAO() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.prepareStatement("""
                    CREATE TABLE IF NOT EXISTS Auth (
                      AuthToken varchar(64) PRIMARY KEY,
                      Username varchar(128) NOT NULL
                    );
                    """).executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Unable to create auth table: " + e);
        }
    }

    /**
     * Adds an object of authorization data to the database
     *
     * @param newAuth the authorization data object to add
     * @throws DataAccessException if authorization token already exists
     */
    public void createAuth(AuthData newAuth) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.prepareStatement(
                    String.format("INSERT INTO Auth (AuthToken, Username) VALUES ('%s', '%s');",
                            newAuth.authToken(), newAuth.username())
            ).executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Unable to create auth data: " + e);
        }
    }

    /**
     * @return object of authorization data by authorization token or null if authorization
     * token does not exist
     *
     * @param authToken the authorization token of the requested data
     */
    public AuthData getAuth(String authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            ResultSet results = conn.prepareStatement(
                    String.format("SELECT Username FROM Auth WHERE AuthToken='%s';", authToken)
            ).executeQuery();
            if (results.next()) { // detect if an object is in the set
                return new AuthData(authToken, results.getString("Username"));
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to get auth data: " + e);
        }
    }

    /**
     * @return ArrayList of all authorization data objects in the database
     */
    public ArrayList<AuthData> listAuths() throws DataAccessException {
        ArrayList<AuthData> resultList = new ArrayList<AuthData>();
        try (Connection conn = DatabaseManager.getConnection()) {
            ResultSet results = conn.prepareStatement(
                    "SELECT AuthToken, Username FROM Auth;").executeQuery();
            while (results.next()) {
                resultList.add(new AuthData(
                        results.getString("AuthToken"),
                        results.getString("Username")));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to list auth data: " + e);
        }
        return resultList;
    }

    /**
     * Deletes an object of authorization data by authorization token
     *
     * @param authToken the authorization token of the object to delete
     */
    public void deleteAuth(String authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.prepareStatement(
                    String.format("DELETE FROM Auth WHERE AuthToken='%s';", authToken)
            ).executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Unable to delete auth data: " + e);
        }
    }

    /**
     * Deletes all authorization data in the database
     */
    public void clear() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.prepareStatement("TRUNCATE Auth;").executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Unable to clear auth table: " + e);
        }
    }
}

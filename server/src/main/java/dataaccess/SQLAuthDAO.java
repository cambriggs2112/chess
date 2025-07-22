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
            throw new DataAccessException("Unable to create authorization table: " + e);
        }
    }

    /**
     * Adds an object of authorization data to the database
     *
     * @param newAuth the authorization data object to add
     * @throws DataAccessException if authorization token is null or already exists
     */
    public void createAuth(AuthData newAuth) throws DataAccessException {
        if (getAuth(newAuth.authToken()) != null) {
            throw new DataAccessException("Unable to create authorization data: Authorization token already exists.");
        }
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.prepareStatement(
                    String.format("INSERT INTO Auth (AuthToken, Username) VALUES ('%s', '%s');",
                            escapeApostrophes(newAuth.authToken()),
                            escapeApostrophes(newAuth.username()))
            ).executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Unable to create authorization data: " + e);
        }
    }

    /**
     * @return object of authorization data by authorization token or null if authorization
     * token does not exist
     *
     * @param authToken the authorization token of the requested data
     * @throws DataAccessException if authorization token is null
     */
    public AuthData getAuth(String authToken) throws DataAccessException {
        if (authToken == null) {
            throw new DataAccessException("Unable to get authorization data: Authorization token cannot be null.");
        }
        try (Connection conn = DatabaseManager.getConnection()) {
            ResultSet results = conn.prepareStatement(
                    String.format("SELECT * FROM Auth WHERE AuthToken='%s';",
                            escapeApostrophes(authToken))).executeQuery();
            if (results.next()) { // detect if an object is in the set
                return new AuthData(authToken, results.getString("Username"));
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to get authorization data: " + e);
        }
    }

    /**
     * @return ArrayList of all authorization data objects in the database
     */
    public ArrayList<AuthData> listAuths() throws DataAccessException {
        ArrayList<AuthData> resultList = new ArrayList<AuthData>();
        try (Connection conn = DatabaseManager.getConnection()) {
            ResultSet results = conn.prepareStatement("SELECT * FROM Auth;").executeQuery();
            while (results.next()) {
                resultList.add(new AuthData(
                        results.getString("AuthToken"),
                        results.getString("Username")));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to list authorization data: " + e);
        }
        return resultList;
    }

    /**
     * Deletes an object of authorization data by authorization token
     *
     * @param authToken the authorization token of the object to delete
     * @throws DataAccessException if authorization token is null or does not exist
     */
    public void deleteAuth(String authToken) throws DataAccessException {
        if (getAuth(authToken) == null) {
            throw new DataAccessException("Unable to delete authorization data: Authorization token not found.");
        }
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.prepareStatement(
                    String.format("DELETE FROM Auth WHERE AuthToken='%s';",
                            escapeApostrophes(authToken))).executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Unable to delete authorization data: " + e);
        }
    }

    /**
     * Deletes all authorization data in the database
     */
    public void clear() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.prepareStatement("TRUNCATE Auth;").executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Unable to clear authorization table: " + e);
        }
    }

    private String escapeApostrophes(String str) {
        String result = str;
        for (int i = 0; i < result.length(); i++) {
            if (result.charAt(i) == '\'') {
                result = result.substring(0, i) + "'" + result.substring(i);
                i++;
            }
        }
        return result;
    }
}

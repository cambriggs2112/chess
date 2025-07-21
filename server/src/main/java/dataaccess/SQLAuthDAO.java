package dataaccess;

import model.AuthData;
import java.util.ArrayList;

/**
 * An SQL-based database of authorization data (authToken, username).
 */
public class SQLAuthDAO implements AuthDAO {
    public SQLAuthDAO() {

    }


    /**
     * Adds an object of authorization data to the database
     *
     * @param newAuth the authorization data object to add
     * @throws DataAccessException if authorization token is null or already exists
     */
    public void createAuth(AuthData newAuth) throws DataAccessException {

    }

    /**
     * @return object of authorization data by authorization token or null if authorization
     * token does not exist
     *
     * @param authToken the authorization token of the requested data
     * @throws DataAccessException if authorization token is null
     */
    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    /**
     * @return ArrayList of all authorization data objects in the database
     */
    public ArrayList<AuthData> listAuths() throws DataAccessException {
        return null;
    }

    /**
     * Deletes an object of authorization data by authorization token
     *
     * @param authToken the authorization token of the object to delete
     * @throws DataAccessException if authorization token is null or does not exist
     */
    public void deleteAuth(String authToken) throws DataAccessException {

    }

    /**
     * Deletes all authorization data in the database
     */
    public void clear() throws DataAccessException {

    }
}

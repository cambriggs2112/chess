package dataaccess;

import model.AuthData;
import java.util.ArrayList;

/**
 * A memory-based database of authorization data (authToken, username) that uses an
 * ArrayList.
 */
public class MemoryAuthDAO implements AuthDAO {
    private ArrayList<AuthData> authDatabase;

    public MemoryAuthDAO() {
        this.authDatabase = new ArrayList<AuthData>();
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
        authDatabase.add(newAuth);
    }

    /**
     * @return object of authorization data by authorization token or null if authorization
     * token does not exist
     *
     * @param authToken the authorization token of the requested data
     * @throws DataAccessException if authorization token is null
     */
    public AuthData getAuth(String authToken) throws DataAccessException {
        for (AuthData a : authDatabase) {
            if (authToken == null) {
                throw new DataAccessException("Unable to get authorization data: Authorization token cannot be null.");
            }
            if (a.authToken().equals(authToken)) {
                return a;
            }
        }
        return null;
    }

    /**
     * @return ArrayList of all authorization data objects in the database
     */
    public ArrayList<AuthData> listAuths() throws DataAccessException {
        return authDatabase;
    }

    /**
     * Replaces an object of authorization data in the database by authorization token
     *
     * @param newAuth the new authorization data object to replace the existing one
     * @throws DataAccessException if authorization token is null or does not exist
     */
    public void updateAuth(AuthData newAuth) throws DataAccessException {
        AuthData a = getAuth(newAuth.authToken());
        if (a == null) {
            throw new DataAccessException("Unable to update authorization data: Authorization token not found.");
        }
        int index = authDatabase.indexOf(a);
        authDatabase.set(index, newAuth);
    }

    /**
     * Deletes an object of authorization data by authorization token
     *
     * @param authToken the authorization token of the object to delete
     * @throws DataAccessException if authorization token is null or does not exist
     */
    public void deleteAuth(String authToken) throws DataAccessException {
        AuthData a = getAuth(authToken);
        if (a == null) {
            throw new DataAccessException("Unable to delete authorization data: Authorization token not found.");
        }
        authDatabase.remove(a);
    }

    /**
     * Deletes all authorization data in the database
     */
    public void clear() throws DataAccessException {
        authDatabase.clear();
    }
}

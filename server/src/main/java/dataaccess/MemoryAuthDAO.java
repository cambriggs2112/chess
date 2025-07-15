package dataaccess;

import model.AuthData;
import java.util.ArrayList;

public class MemoryAuthDAO implements AuthDAO {
    private ArrayList<AuthData> authDatabase;

    public MemoryAuthDAO() {
        this.authDatabase = new ArrayList<AuthData>();
    }

    public void createAuth(AuthData newAuth) throws DataAccessException {
        if (getAuth(newAuth.authToken()) != null) {
            throw new DataAccessException("Unable to create authorization data: Authorization token already exists.");
        }
        authDatabase.add(newAuth);
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        for (AuthData a : authDatabase) {
            if (a.authToken().equals(authToken)) {
                return a;
            }
        }
        return null;
    }

    public ArrayList<AuthData> listAuths() throws DataAccessException {
        return authDatabase;
    }

    public void updateAuth(AuthData newAuth) throws DataAccessException {
        AuthData a = getAuth(newAuth.authToken());
        if (a == null) {
            throw new DataAccessException("Unable to update authorization data: Authorization token not found.");
        }
        int index = authDatabase.indexOf(a);
        authDatabase.set(index, newAuth);
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        AuthData a = getAuth(authToken);
        if (a == null) {
            throw new DataAccessException("Unable to delete authorization data: Authorization token not found.");
        }
        authDatabase.remove(a);
    }

    public void clear() throws DataAccessException {
        authDatabase.clear();
    }
}

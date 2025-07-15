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
            throw new DataAccessException("authToken already exists for createAuth()");
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

    public void updateAuth(String authToken) throws DataAccessException {
        AuthData a = getAuth(authToken);
        if (a == null) {
            throw new DataAccessException("authToken not found for updateAuth()");
        }
        int index = authDatabase.indexOf(a);
        authDatabase.set(index, a);
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        AuthData a = getAuth(authToken);
        if (a == null) {
            throw new DataAccessException("authToken not found for deleteAuth()");
        }
        authDatabase.remove(a);
    }

    public void clear() throws DataAccessException {
        authDatabase.clear();
    }
}

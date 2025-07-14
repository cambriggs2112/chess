package dataaccess;

import java.util.ArrayList;

public class AuthDAO {
    private ArrayList<AuthData> authDatabase;

    public AuthDAO() {
        this.authDatabase = new ArrayList<AuthData>();
    }

    public void createAuth(AuthData newAuth) throws DataAccessException {
        if (getAuth(newAuth.getAuthToken()) != null) {
            throw new DataAccessException("authToken already exists for createAuth()");
        }
        authDatabase.add(newAuth);
    }

    public AuthData getAuth(String authToken) {
        for (AuthData a : authDatabase) {
            if (a.getAuthToken().equals(authToken)) {
                return a;
            }
        }
        return null;
    }

    public ArrayList<AuthData> listAuths() {
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

    public void clear() {
        authDatabase.clear();
    }
}

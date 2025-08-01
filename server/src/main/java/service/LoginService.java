package service;

import dataaccess.*;
import model.*;
import model.request.LoginRequest;
import model.result.LoginResult;
import org.mindrot.jbcrypt.BCrypt;
import java.util.UUID;

/**
 * A service that logs users in.
 * A username and password are provided by the user, and if both are correct, an authorization token is obtained.
 */
public class LoginService {

    public LoginService() {}

    /**
     * Logs a user in with a username and password to obtain an authorization token.
     * Adds authorization data to the database.
     *
     * @param request the request object (username, password)
     * @return a result object (username, authToken)
     * @throws ServiceException if required fields are missing (400), username or password is
     *                incorrect (401), or error occurs with data access (500)
     */
    public LoginResult login(LoginRequest request) throws ServiceException {
        String authToken = null;
        if (request.username() == null || request.username().isEmpty()) {
            throw new ServiceException("ERROR: Bad Request: Username is required to login.", 400);
        }
        if (request.password() == null || request.password().isEmpty()) {
            throw new ServiceException("ERROR: Bad Request: Password is required to login.", 400);
        }
        try {
            SQLAuthDAO auth = new SQLAuthDAO();
            SQLUserDAO user = new SQLUserDAO();
            UserData thisUser = user.getUser(request.username());
            if (thisUser == null) {
                throw new ServiceException("ERROR: Unauthorized: Incorrect username provided whilst attempting to login.", 401);
            }
            if (!BCrypt.checkpw(request.password(), thisUser.password())) { // database password is hashed
                throw new ServiceException("ERROR: Unauthorized: Incorrect password provided whilst attempting to login.", 401);
            }
            while (authToken == null || auth.getAuth(authToken) != null) { // Used to effectively guarantee authToken is unique
                authToken = UUID.randomUUID().toString();
            }
            authToken = UUID.randomUUID().toString();
            AuthData newAuth = new AuthData(authToken, request.username());
            auth.createAuth(newAuth);
        } catch (DataAccessException e) {
            throw new ServiceException("ERROR: Internal Server Error occurred whilst attempting to login: " + e.getMessage(), 500);
        }
        return new LoginResult(request.username(), authToken);
    }
}

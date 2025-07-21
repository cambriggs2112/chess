package service;

import dataaccess.*;
import model.*;
import java.util.UUID;

/**
 * A service that registers new users.
 * A username, password, and email are provided to add a new user.
 * An authorization token is obtained to automatically log the user in.
 */
public class RegisterService {
    public record RegisterRequest(String username, String password, String email) {}
    public record RegisterResult(String username, String authToken) {}

    public RegisterService() {}

    /**
     * Registers a new user and logs the new user in.
     * Adds user data and authorization data to the database.
     *
     * @param request the request object (username, password, email)
     * @return a result object (username, authToken)
     * @throws ServiceException if required fields are missing (400), username is already taken
     *                (403), or error occurs with data access (500)
     */
    public RegisterResult register(RegisterRequest request) throws ServiceException {
        String authToken = null;
        if (request.username() == null || request.username().isEmpty()) {
            throw new ServiceException("ERROR: Bad Request: Username is required to register.", 400);
        }
        if (request.password() == null || request.password().isEmpty()) {
            throw new ServiceException("ERROR: Bad Request: Password is required to register.", 400);
        }
        if (request.email() == null || request.email().isEmpty()) {
            throw new ServiceException("ERROR: Bad Request: Email is required to register.", 400);
        }
        try {
            MemoryAuthDAO auth = new MemoryAuthDAO();
            MemoryUserDAO user = new MemoryUserDAO();
            if (user.getUser(request.username()) != null) {
                throw new ServiceException("ERROR: Forbidden: Unable to register since provided username is already taken.", 403);
            } // Put hash here
            UserData newUser = new UserData(request.username(), request.password(), request.email());
            user.createUser(newUser);
            while (authToken == null || auth.getAuth(authToken) != null) { // Used to effectively guarantee authToken is unique
                authToken = UUID.randomUUID().toString();
            }
            AuthData newAuth = new AuthData(authToken, request.username());
            auth.createAuth(newAuth);
        } catch (DataAccessException e) {
            throw new ServiceException("ERROR: Internal Server Error occurred whilst attempting to register: " + e, 500);
        }
        return new RegisterResult(request.username(), authToken);
    }
}

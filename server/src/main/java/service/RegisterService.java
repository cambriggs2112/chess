package service;

import dataaccess.*;
import model.*;
import java.util.UUID;

public class RegisterService {
    public record RegisterRequest(String username, String password, String email) {}
    public record RegisterResult(String username, String authToken) {}

    private AuthDAO auth;
    private GameDAO game;
    private UserDAO user;

    public RegisterService(AuthDAO auth, GameDAO game, UserDAO user) {
        this.auth = auth;
        this.game = game;
        this.user = user;
    }

    public RegisterResult register(RegisterRequest request) throws ServiceException {
        String authToken = null;
        if (request.username() == null || request.username().isEmpty()) {
            throw new ServiceException("Bad Request: Username is required to register.", 400);
        }
        if (request.password() == null || request.password().isEmpty()) {
            throw new ServiceException("Bad Request: Password is required to register.", 400);
        }
        if (request.email() == null || request.email().isEmpty()) {
            throw new ServiceException("Bad Request: Email is required to register.", 400);
        }
        try {
            if (user.getUser(request.username()) != null) {
                throw new ServiceException("Forbidden: Unable to register since provided username is already taken.", 403);
            }
            UserData newUser = new UserData(request.username(), request.password(), request.email());
            user.createUser(newUser);
            while (authToken == null || auth.getAuth(authToken) != null) { // Used to effectively guarantee authToken is unique
                authToken = UUID.randomUUID().toString();
            }
            AuthData newAuth = new AuthData(authToken, request.username());
            auth.createAuth(newAuth);
        } catch (DataAccessException e) {
            throw new ServiceException("Internal Server Error occurred whilst attempting to register: " + e, 500);
        }
        return new RegisterResult(request.username(), authToken);
    }
}

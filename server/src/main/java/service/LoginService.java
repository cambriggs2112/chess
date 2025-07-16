package service;

import dataaccess.*;
import model.*;
import java.util.UUID;

public class LoginService {
    public record LoginRequest(String username, String password) {}
    public record LoginResult(String username, String authToken) {}

    private AuthDAO auth;
    private GameDAO game;
    private UserDAO user;

    public LoginService(AuthDAO auth, GameDAO game, UserDAO user) {
        this.auth = auth;
        this.game = game;
        this.user = user;
    }

    public LoginResult login(LoginRequest request) throws ServiceException {
        String authToken = null;
        if (request.username() == null || request.username().isEmpty()) {
            throw new ServiceException("Bad Request: Username is required to login.", 400);
        }
        if (request.password() == null || request.password().isEmpty()) {
            throw new ServiceException("Bad Request: Password is required to login.", 400);
        }
        try {
            UserData thisUser = user.getUser(request.username());
            if (thisUser == null) {
                throw new ServiceException("Unauthorized: Incorrect username provided whilst attempting to login.", 401);
            }
            if (!request.password().equals(thisUser.password())) {
                throw new ServiceException("Unauthorized: Incorrect password provided whilst attempting to login.", 401);
            }
            while (authToken == null || auth.getAuth(authToken) != null) { // Used to effectively guarantee authToken is unique
                authToken = UUID.randomUUID().toString();
            }
            authToken = UUID.randomUUID().toString();
            AuthData newAuth = new AuthData(authToken, request.username());
            auth.createAuth(newAuth);
        } catch (DataAccessException e) {
            throw new ServiceException("Internal Server Error occurred whilst attempting to login: " + e, 500);
        }
        return new LoginResult(request.username(), authToken);
    }
}

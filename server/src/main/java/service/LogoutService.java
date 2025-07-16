package service;

import dataaccess.*;

public class LogoutService {
    public record LogoutRequest(String authToken) {}
    public record LogoutResult() {}

    private AuthDAO auth;
    private GameDAO game;
    private UserDAO user;

    public LogoutService(AuthDAO auth, GameDAO game, UserDAO user) {
        this.auth = auth;
        this.game = game;
        this.user = user;
    }

    public LogoutResult logout(LogoutRequest request) throws ServiceException {
        try {
            if (request.authToken() == null || auth.getAuth(request.authToken()) == null) {
                throw new ServiceException("Unauthorized: Unknown authorization token provided whilst attempting to logout.", 401);
            }
            auth.deleteAuth(request.authToken());
        } catch (DataAccessException e) {
            throw new ServiceException("Internal Server Error occurred whilst attempting to logout: " + e, 500);
        }
        return new LogoutResult();
    }
}

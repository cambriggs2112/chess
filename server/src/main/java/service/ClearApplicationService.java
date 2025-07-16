package service;

import dataaccess.*;

public class ClearApplicationService {
    public record ClearApplicationRequest() {}
    public record ClearApplicationResult() {}

    private AuthDAO auth;
    private GameDAO game;
    private UserDAO user;

    public ClearApplicationService(AuthDAO auth, GameDAO game, UserDAO user) {
        this.auth = auth;
        this.game = game;
        this.user = user;
    }

    public ClearApplicationResult clearApplication(ClearApplicationRequest request) throws ServiceException {
        try {
            auth.clear();
            game.clear();
            user.clear();
        } catch (DataAccessException e) {
            throw new ServiceException("Internal Server Error occurred whilst attempting to clear application: " + e, 500);
        }
        return new ClearApplicationResult();
    }
}

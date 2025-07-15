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

    public ClearApplicationResult clearApplication(ClearApplicationRequest request) throws InternalServerErrorException {
        try {
            auth.clear();
            game.clear();
            user.clear();
        } catch (DataAccessException e) {
            throw new InternalServerErrorException("[500] Internal Server Error occurred whilst attempting to clear application: " + e);
        }
        return new ClearApplicationResult();
    }
}

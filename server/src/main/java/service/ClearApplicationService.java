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

    public ClearApplicationResult clearApplication(ClearApplicationRequest request) {

        return null;
    }
}

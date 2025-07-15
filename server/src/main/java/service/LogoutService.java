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

    public LogoutResult logout(LogoutRequest request) throws UnauthorizedException, InternalServerErrorException {
        try {
            if (auth.getAuth(request.authToken()) == null) {
                throw new UnauthorizedException("[401] Unauthorized: Unknown authorization token provided whilst attempting to logout.");
            }
            auth.deleteAuth(request.authToken());
        } catch (DataAccessException e) {
            throw new InternalServerErrorException("[500] Internal Server Error occurred whilst attempting to logout: " + e);
        }
        return new LogoutResult();
    }
}

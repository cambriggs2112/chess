package service;

import dataaccess.*;

/**
 * A service that clears the application.
 */
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

    /**
     * Clears all databases in the application
     *
     * @param request the request object
     * @return a result object
     * @throws ServiceException if error occurs with data access (500)
     */
    public ClearApplicationResult clearApplication(ClearApplicationRequest request) throws ServiceException {
        try {
            auth.clear();
            game.clear();
            user.clear();
        } catch (DataAccessException e) {
            throw new ServiceException("ERROR: Internal Server Error occurred whilst attempting to clear application: " + e, 500);
        }
        return new ClearApplicationResult();
    }
}

package service;

import dataaccess.*;

/**
 * A service that clears the application.
 */
public class ClearApplicationService {
    public record ClearApplicationRequest() {}
    public record ClearApplicationResult() {}

    public ClearApplicationService() {}

    /**
     * Clears all databases in the application
     *
     * @param request the request object
     * @return a result object
     * @throws ServiceException if error occurs with data access (500)
     */
    public ClearApplicationResult clearApplication(ClearApplicationRequest request) throws ServiceException {
        try {
            MemoryAuthDAO auth = new MemoryAuthDAO();
            MemoryGameDAO game = new MemoryGameDAO();
            MemoryUserDAO user = new MemoryUserDAO();
            auth.clear();
            game.clear();
            user.clear();
        } catch (DataAccessException e) {
            throw new ServiceException("ERROR: Internal Server Error occurred whilst attempting to clear application: " + e, 500);
        }
        return new ClearApplicationResult();
    }
}

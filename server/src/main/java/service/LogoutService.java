package service;

import dataaccess.*;
import model.*;
import model.Request.LogoutRequest;
import model.Result.LogoutResult;

/**
 * A service that logs authenticated users out.
 */
public class LogoutService {

    public LogoutService() {}

    /**
     * Logs a user out and deletes authorization data from the database.
     *
     * @param request the request object (authToken)
     * @return a result object
     * @throws ServiceException if authorization token is incorrect (401) or error occurs with
     *                data access (500)
     */
    public LogoutResult logout(LogoutRequest request) throws ServiceException {
        try {
            SQLAuthDAO auth = new SQLAuthDAO();
            if (request.authToken() == null || auth.getAuth(request.authToken()) == null) {
                throw new ServiceException("ERROR: Unauthorized: Unknown authorization token provided whilst attempting to logout.", 401);
            }
            auth.deleteAuth(request.authToken());
        } catch (DataAccessException e) {
            throw new ServiceException("ERROR: Internal Server Error occurred whilst attempting to logout: " + e.getMessage(), 500);
        }
        return new LogoutResult();
    }
}

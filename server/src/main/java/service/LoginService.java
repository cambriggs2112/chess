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

    public LoginResult login(LoginRequest request) throws BadRequestException, UnauthorizedException, InternalServerErrorException {
        String authToken;
        if (request.username().isEmpty()) {
            throw new BadRequestException("[400] Bad Request: Username is required to login.");
        }
        if (request.password().isEmpty()) {
            throw new BadRequestException("[400] Bad Request: Password is required to login.");
        }
        try {
            UserData thisUser = user.getUser(request.username());
            if (thisUser == null) {
                throw new UnauthorizedException("[401] Unauthorized: Incorrect username provided whilst attempting to login.");
            }
            if (!request.password().equals(thisUser.password())) {
                throw new UnauthorizedException("[401] Unauthorized: Incorrect password provided whilst attempting to login.");
            }
            authToken = UUID.randomUUID().toString();
            AuthData newAuth = new AuthData(authToken, request.username());
            auth.createAuth(newAuth);
        } catch (DataAccessException e) {
            throw new InternalServerErrorException("[500] Internal Server Error occurred whilst attempting to login: " + e);
        }
        return new LoginResult(request.username(), authToken);
    }
}

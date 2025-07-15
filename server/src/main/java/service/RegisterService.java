package service;

import dataaccess.*;
import model.*;
import java.util.UUID;

public class RegisterService {
    public record RegisterRequest(String username, String password, String email) {}
    public record RegisterResult(String username, String authToken) {}

    private AuthDAO auth;
    private GameDAO game;
    private UserDAO user;

    public RegisterService(AuthDAO auth, GameDAO game, UserDAO user) {
        this.auth = auth;
        this.game = game;
        this.user = user;
    }

    public RegisterResult register(RegisterRequest request) throws BadRequestException, ForbiddenException, InternalServerErrorException {
        String authToken;
        if (request.username().isEmpty()) {
            throw new BadRequestException("[400] Bad Request: Username is required to register.");
        }
        if (request.password().isEmpty()) {
            throw new BadRequestException("[400] Bad Request: Password is required to register.");
        }
        if (request.email().isEmpty()) {
            throw new BadRequestException("[400] Bad Request: Email is required to register.");
        }
        try {
            if (user.getUser(request.username()) != null) {
                throw new ForbiddenException("[403] Forbidden: Unable to register since provided username is already taken.");
            }
            UserData newUser = new UserData(request.username(), request.password(), request.email());
            user.createUser(newUser);
            authToken = UUID.randomUUID().toString();
            AuthData newAuth = new AuthData(authToken, request.username());
            auth.createAuth(newAuth);
        } catch (DataAccessException e) {
            throw new InternalServerErrorException("[500] Internal Server Error occurred whilst attempting to register: " + e);
        }
        return new RegisterResult(request.username(), authToken);
    }
}

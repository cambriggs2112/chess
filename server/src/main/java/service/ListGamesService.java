package service;

import java.util.ArrayList;
import dataaccess.*;
import model.GameData;

public class ListGamesService {
    public record ListGamesRequest(String authToken) {}
    public record ListGamesResult(ArrayList<GameData> games) {}

    private AuthDAO auth;
    private GameDAO game;
    private UserDAO user;

    public ListGamesService(AuthDAO auth, GameDAO game, UserDAO user) {
        this.auth = auth;
        this.game = game;
        this.user = user;
    }

    public ListGamesResult listGames(ListGamesRequest request) {
        return null;
    }
}

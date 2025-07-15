package server;

import com.google.gson.Gson;
import service.ListGamesService;
import service.ListGamesService.*;

public class ListGamesHandler {
    private ListGamesService listGamesService;

    public ListGamesHandler(ListGamesService listGamesService) {
        this.listGamesService = listGamesService;
    }
}

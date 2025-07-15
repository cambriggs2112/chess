package server;

import com.google.gson.Gson;
import service.CreateGameService;
import service.CreateGameService.*;

public class CreateGameHandler {
    private CreateGameService createGameService;

    public CreateGameHandler(CreateGameService createGameService) {
        this.createGameService = createGameService;
    }
}

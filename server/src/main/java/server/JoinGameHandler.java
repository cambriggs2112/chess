package server;

import com.google.gson.Gson;
import service.JoinGameService;
import service.JoinGameService.*;

public class JoinGameHandler {
    private JoinGameService joinGameService;

    public JoinGameHandler(JoinGameService joinGameService) {
        this.joinGameService = joinGameService;
    }
}

package websocket.commands;

import chess.*;

import java.util.Objects;

public class MakeMoveCommand extends UserGameCommand {
    private final ChessMove move;

    public MakeMoveCommand(String authToken, Integer gameID, ChessMove move) {
        super(CommandType.MAKE_MOVE, authToken, gameID);
        this.move = move;
    }

    public ChessMove getMove() {
        return move;
    }
}

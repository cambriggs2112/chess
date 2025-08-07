package websocket.messages;

import chess.*;

public class LoadGameMessage extends ServerMessage {
    private final ChessGame game;
    private final ChessMove move;

    public LoadGameMessage(ChessGame game, ChessMove move) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
        this.move = move;
    }

    public ChessGame getGame() {
        return game;
    }

    public ChessMove getMove() {
        return move;
    }
}

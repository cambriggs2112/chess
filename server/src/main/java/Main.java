import chess.*;
import server.Server;
import dataaccess.*;
import service.*;

public class Main {
    public static void main(String[] args) {
//        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
//        System.out.println("♕ 240 Chess Server: " + piece);
//        Server test = new Server();
//        test.run(8081);

        MemoryAuthDAO auths = new MemoryAuthDAO();
        MemoryGameDAO games = new MemoryGameDAO();
        MemoryUserDAO users = new MemoryUserDAO();

        ClearApplicationService c = new ClearApplicationService(auths, games, users);
    }
}
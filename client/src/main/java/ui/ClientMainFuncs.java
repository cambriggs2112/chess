package ui;

import java.util.ArrayList;
import java.util.Scanner;
import model.*;
import chess.*;
import model.request.*;
import model.result.ListGamesResult;
import model.result.ListGamesResultElement;
import model.result.LoginResult;
import model.result.RegisterResult;
import websocket.commands.*;

public class ClientMainFuncs {
    private static final String SERVER_URL = "http://localhost:8081";
    private static ArrayList<Integer> gameIDs = new ArrayList<Integer>();
    private static ArrayList<String> gameNames = new ArrayList<String>();
    private static ChessGame gameObj = null;

    public static ArrayList<String> parseInput(String line) {
        ArrayList<String> result = new ArrayList<String>();
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == ' ') {
                result.add(line.substring(0, i));
                line = line.substring(i + 1);
                i = -1;
            }
        }
        result.add(line);
        if (!result.getFirst().equalsIgnoreCase("create")) {
            for (int i = 0; i < result.size(); i++) {
                if (result.get(i).isEmpty()) {
                    result.remove(i);
                    i--;
                }
            }
        }
        return result;
    }

    public static String login(ArrayList<String> arguments) {
        if (arguments.size() < 3) {
            System.out.println("\u001b[38;5;160m  Usage: login <USERNAME> <PASSWORD>\u001b[39m");
            return null;
        }
        String username = arguments.get(1);
        String password = arguments.get(2);
        try {
            ServerFacade sf = new ServerFacade(SERVER_URL);
            LoginResult res = sf.login(new LoginRequest(username, password));
            System.out.println("\u001b[38;5;12m  Logged in as " + res.username() + "\u001b[39m");
            return res.authToken();
        } catch (ServiceException e) {
            System.out.println("\u001b[38;5;160m  " + e.getMessage() + "\u001b[39m");
            return null;
        }
    }

    public static String register(ArrayList<String> arguments) {
        if (arguments.size() < 4) {
            System.out.println("\u001b[38;5;160m  Usage: register <USERNAME> <PASSWORD> <EMAIL>\u001b[39m");
            return null;
        }
        String username = arguments.get(1);
        String password = arguments.get(2);
        String email = arguments.get(3);
        try {
            ServerFacade sf = new ServerFacade(SERVER_URL);
            RegisterResult res = sf.register(new RegisterRequest(username, password, email));
            System.out.println("\u001b[38;5;12m  Logged in as " + res.username() + "\u001b[39m");
            return res.authToken();
        } catch (ServiceException e) {
            System.out.println("\u001b[38;5;160m  " + e.getMessage() + "\u001b[39m");
            return null;
        }
    }

    public static void logout(String authToken, boolean print) {
        try {
            ServerFacade sf = new ServerFacade(SERVER_URL);
            sf.logout(new LogoutRequest(authToken));
            if (print) {
                System.out.println("\u001b[38;5;12m  Logged out\u001b[39m");
            }
        } catch (ServiceException e) {
            System.out.println("\u001b[38;5;160m  " + e.getMessage() + "\u001b[39m");
        }
    }

    public static void createGame(ArrayList<String> arguments, String authToken) {
        String gameName = "";
        for (int i = 1; i < arguments.size(); i++) {
            gameName += arguments.get(i);
            if (i + 1 < arguments.size()) {
                gameName += " ";
            }
        }
        if (gameName.isEmpty()) {
            System.out.println("\u001b[38;5;160m  Usage: create <NAME>\u001b[39m");
            return;
        }
        try {
            ServerFacade sf = new ServerFacade(SERVER_URL);
            sf.createGame(new CreateGameRequest(authToken, gameName));
            System.out.println("\u001b[38;5;12m  Successfully created game " + gameName + "\u001b[39m");
        } catch (ServiceException e) {
            System.out.println("\u001b[38;5;160m  " + e.getMessage() + "\u001b[39m");
        }
    }

    public static void listGames(String authToken) {
        try {
            ServerFacade sf = new ServerFacade(SERVER_URL);
            ListGamesResult res = sf.listGames(new ListGamesRequest(authToken));
            if (res.games().isEmpty()) {
                System.out.println("\u001b[38;5;12m  No games to display.\u001b[39m");
                System.out.println();
                return;
            }
            int longestID = Math.max(2, Integer.toString(res.games().size()).length());
            int longestName = 4;
            int longestWhite = 14;
            for (ListGamesResultElement game : res.games()) {
                if (game.gameName().length() > longestName) {
                    longestName = game.gameName().length();
                }
                if (game.whiteUsername() != null && game.whiteUsername().length() > longestWhite) {
                    longestWhite = game.whiteUsername().length();
                }
            }
            System.out.print("\u001b[38;5;12m  ID");
            for (int i = 2; i < longestID; i++) {
                System.out.print(" ");
            }
            System.out.print(" Name");
            for (int i = 4; i < longestName; i++) {
                System.out.print(" ");
            }
            System.out.print(" White Username");
            for (int i = 14; i < longestWhite; i++) {
                System.out.print(" ");
            }
            System.out.println(" Black Username\u001b[39m");
            gameIDs.clear();
            gameNames.clear();
            int n = 0;
            for (ListGamesResultElement game : res.games()) {
                n++;
                gameIDs.add(game.gameID());
                gameNames.add(game.gameName());
                System.out.print("\u001b[38;5;12m  " + n);
                for (int i = Integer.toString(n).length(); i < longestID; i++) {
                    System.out.print(" ");
                }
                System.out.print(" ");
                System.out.print(game.gameName());
                for (int i = game.gameName().length(); i < longestName; i++) {
                    System.out.print(" ");
                }
                System.out.print(" ");
                int whiteLength = 0;
                if (game.whiteUsername() != null) {
                    System.out.print(game.whiteUsername());
                    whiteLength = game.whiteUsername().length();
                }
                if (game.blackUsername() == null) {
                    System.out.println("\u001b[39m");
                    continue;
                }
                System.out.print(" ");
                for (int i = whiteLength; i < longestWhite; i++) {
                    System.out.print(" ");
                }
                System.out.print(game.blackUsername());
                System.out.println("\u001b[39m");
            }
            System.out.println();
        } catch (ServiceException e) {
            System.out.println("\u001b[38;5;160m  " + e.getMessage() + "\u001b[39m");
        }
    }

    public static void joinGame(ArrayList<String> arguments, String authToken, String username) {
        int gameNum;
        ChessGame.TeamColor color;
        if (arguments.size() < 3) {
            System.out.println("\u001b[38;5;160m  Usage: join <ID> [WHITE|BLACK]\u001b[39m");
            return;
        }
        try {
            gameNum = Integer.parseInt(arguments.get(1));
        } catch (NumberFormatException e) {
            System.out.println("\u001b[38;5;160m  Invalid ID entered. Try List for a list of games with their IDs.\u001b[39m");
            return;
        }
        if (gameNum <= 0 || gameNum > gameIDs.size()) {
            System.out.println("\u001b[38;5;160m  Invalid ID entered. Try List for a list of games with their IDs.\u001b[39m");
            return;
        }
        if (arguments.get(2).equalsIgnoreCase("white")) {
            color = ChessGame.TeamColor.WHITE;
        } else if (arguments.get(2).equalsIgnoreCase("black")) {
            color = ChessGame.TeamColor.BLACK;
        } else {
            System.out.println("\u001b[38;5;160m  Invalid color entered. Color must be WHITE or BLACK.\u001b[39m");
            return;
        }
        try {
            ServerFacade sf = new ServerFacade(SERVER_URL);
            sf.joinGame(new JoinGameRequest(authToken, color, gameIDs.get(gameNum - 1)));
        } catch (ServiceException e) {
            System.out.println("\u001b[38;5;160m  " + e.getMessage() + "\u001b[39m");
            return;
        }
        gameplayLoop(color, authToken, username, gameNum);
    }

    public static void observeGame(ArrayList<String> arguments, String authToken, String username) {
        int gameNum;
        if (arguments.size() < 2) {
            System.out.println("\u001b[38;5;160m  Usage: observe <ID>\u001b[39m");
            return;
        }
        try {
            gameNum = Integer.parseInt(arguments.get(1));
        } catch (NumberFormatException e) {
            System.out.println("\u001b[38;5;160m  Invalid ID entered. Try List for a list of games with their IDs.\u001b[39m");
            return;
        }
        if (gameNum <= 0 || gameNum > gameIDs.size()) {
            System.out.println("\u001b[38;5;160m  Invalid ID entered. Try List for a list of games with their IDs.\u001b[39m");
            return;
        }
        gameplayLoop(null, authToken, username, gameNum);
    }

    public static void printBoard(ChessGame.TeamColor color, ArrayList<ChessMove> movesToHighlight) {
        ChessBoard board = gameObj.getBoard();
        if (color == ChessGame.TeamColor.BLACK) {
            System.out.println("\u2009     h\u2003 g\u2003 f\u2003 e\u2003 d\u2003 c\u2003 b\u2003 a");
            for (int row = 1; row <= 8; row++) {
                System.out.print("  " + row + " ");
                for (int col = 8; col >= 1; col--) {
                    printPiece(board, new ChessPosition(row, col), movesToHighlight);
                }
                System.out.println("\u001b[39m\u001b[49m " + row + " ");
            }
            System.out.println("\u2009     h\u2003 g\u2003 f\u2003 e\u2003 d\u2003 c\u2003 b\u2003 a");
        } else {
            System.out.println("\u2009     a\u2003 b\u2003 c\u2003 d\u2003 e\u2003 f\u2003 g\u2003 h");
            for (int row = 8; row >= 1; row--) {
                System.out.print("  " + row + " ");
                for (int col = 1; col <= 8; col++) {
                    printPiece(board, new ChessPosition(row, col), movesToHighlight);
                }
                System.out.println("\u001b[39m\u001b[49m " + row + " ");
            }
            System.out.println("\u2009     a\u2003 b\u2003 c\u2003 d\u2003 e\u2003 f\u2003 g\u2003 h");
            }
        System.out.println();
    }

    private static void printPiece(ChessBoard board, ChessPosition pos, ArrayList<ChessMove> movesToHighlight) {
        System.out.print("\u001b[48;5;" + backgroundColorType(pos, movesToHighlight) + "m");
        ChessPiece piece = board.getPiece(pos);
        if (piece == null) {
            System.out.print(" \u2003 ");
            return;
        }
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            System.out.print("\u001b[38;5;15m");
            switch (piece.getPieceType()) {
                case KING -> System.out.print(" ♔ ");
                case QUEEN -> System.out.print(" ♕ ");
                case BISHOP -> System.out.print(" ♗ ");
                case KNIGHT -> System.out.print(" ♘ ");
                case ROOK -> System.out.print(" ♖ ");
                case PAWN -> System.out.print(" ♙ ");
            }
        } else {
            System.out.print("\u001b[38;5;0m");
            switch (piece.getPieceType()) {
                case KING -> System.out.print(" ♚ ");
                case QUEEN -> System.out.print(" ♛ ");
                case BISHOP -> System.out.print(" ♝ ");
                case KNIGHT -> System.out.print(" ♞ ");
                case ROOK -> System.out.print(" ♜ ");
                case PAWN -> System.out.print(" ♟ ");
            }
        }
    }

    // 235 = dark gray
    // 242 = light gray
    // 22 = dark green
    // 46 = light green
    // 12 = blue
    private static int backgroundColorType(ChessPosition pos, ArrayList<ChessMove> movesToHighlight) {
        if (movesToHighlight != null) {
            for (ChessMove move : movesToHighlight) {
                if (move.getStartPosition().equals(pos)) {
                    return 12;
                }
                if (move.getEndPosition().equals(pos)) {
                    return ((pos.getRow() + pos.getColumn()) % 2 == 0) ? 22 : 46;
                }
            }
        }
        return ((pos.getRow() + pos.getColumn()) % 2 == 0) ? 235 : 242;
    }

    // color is null if observer
    private static void gameplayLoop(ChessGame.TeamColor color, String authToken, String username, int gameNum) {
        String gameName = gameNames.get(gameNum - 1);
        int gameID = gameIDs.get(gameNum - 1);
        Scanner input = new Scanner(System.in);
        WebSocketClient ws;
        try {
            ws = new WebSocketClient(SERVER_URL.replace("http", "ws") + "/ws", color, username, gameName);
        } catch (Exception e) {
            System.out.println("\u001b[38;5;160m  Unable to enter gameplay: " + e.getMessage() + "\u001b[39m");
            return;
        }
        ws.sendCommand(new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID));
        while (true) {
            System.out.print("[" + username + "." + gameName + "] >>> ");
            ArrayList<String> arguments = parseInput(input.nextLine());
            if (arguments.isEmpty()) {
                continue;
            }
            if (arguments.getFirst().equalsIgnoreCase("help")) {
                System.out.println("\u001b[38;5;12m  highlight <SOURCE>\u001b[39m - legal moves (e.g. f5)");
                if (color != null) {
                    System.out.println("\u001b[38;5;12m  move <SOURCE> <DESTINATION> " +
                            "[QUEEN|BISHOP|KNIGHT|ROOK|(empty)]\u001b[39m - make a move (e.g. f5 e4 QUEEN)");
                }
                System.out.println("\u001b[38;5;12m  redraw\u001b[39m - chess board");
                if (color != null) {
                    System.out.println("\u001b[38;5;12m  resign\u001b[39m - from game");
                }
                System.out.println("\u001b[38;5;12m  leave\u001b[39m - when you are done");
                System.out.println("\u001b[38;5;12m  help\u001b[39m - with possible commands");
                System.out.println();
            } else if (arguments.getFirst().equalsIgnoreCase("redraw")) {
                printBoard(color, null);
            } else if (arguments.getFirst().equalsIgnoreCase("leave")) {
                ws.sendCommand(new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID));
                ws.close();
                break;
            } else if (arguments.getFirst().equalsIgnoreCase("move") && color != null) {
                if (arguments.size() < 3) {
                    System.out.println("\u001b[38;5;160m  Usage: move <SOURCE> <DESTINATION> [QUEEN|BISHOP|KNIGHT|ROOK|(empty)]\u001b[39m");
                    continue;
                }
                if (!(checkPos(arguments.get(1)) && checkPos(arguments.get(2)))) {
                    System.out.println("\u001b[38;5;160m  Invalid position notation\u001b[39m");
                    continue;
                }
                if (arguments.size() < 4) {
                    ws.sendCommand(new MakeMoveCommand(authToken, gameID,
                            new ChessMove(parsePos(arguments.get(1)), parsePos(arguments.get(2)), null)));
                } else {
                    if (arguments.get(3).equalsIgnoreCase("queen")) {
                        ws.sendCommand(new MakeMoveCommand(authToken, gameID,
                                new ChessMove(parsePos(arguments.get(1)), parsePos(arguments.get(2)), ChessPiece.PieceType.QUEEN)));
                    } else if (arguments.get(3).equalsIgnoreCase("bishop")) {
                        ws.sendCommand(new MakeMoveCommand(authToken, gameID,
                                new ChessMove(parsePos(arguments.get(1)), parsePos(arguments.get(2)), ChessPiece.PieceType.BISHOP)));
                    } else if (arguments.get(3).equalsIgnoreCase("knight")) {
                        ws.sendCommand(new MakeMoveCommand(authToken, gameID,
                                new ChessMove(parsePos(arguments.get(1)), parsePos(arguments.get(2)), ChessPiece.PieceType.KNIGHT)));
                    } else if (arguments.get(3).equalsIgnoreCase("rook")) {
                        ws.sendCommand(new MakeMoveCommand(authToken, gameID,
                                new ChessMove(parsePos(arguments.get(1)), parsePos(arguments.get(2)), ChessPiece.PieceType.ROOK)));
                    } else {
                        System.out.println("\u001b[38;5;160m  Invalid promotion piece. Must be QUEEN, BISHOP, KNIGHT, or ROOK.\u001b[39m");
                    } // Note: KING or PAWN is never a valid promotion.
                }
            } else if (arguments.getFirst().equalsIgnoreCase("resign") && color != null) {
                ws.sendCommand(new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID));
            } else if (arguments.getFirst().equalsIgnoreCase("highlight")) {
                if (arguments.size() < 2) {
                    System.out.println("\u001b[38;5;160m  Usage: highlight <SOURCE>\u001b[39m");
                    continue;
                }
                if (!(checkPos(arguments.get(1)))) {
                    System.out.println("\u001b[38;5;160m  Invalid position notation\u001b[39m");
                    continue;
                }
                if (gameObj.getGameActive()) {
                    printBoard(color, (ArrayList<ChessMove>) gameObj.validMoves(parsePos(arguments.get(1))));
                } else {
                    printBoard(color, null);
                }
            } else {
                System.out.println("\u001b[38;5;160m  Unknown command. Type Help for a list of commands.\u001b[39m");
            }
        }
    }

    private static boolean checkPos(String input) {
        input = input.toLowerCase();
        return input.length() == 2 &&
                (input.charAt(0) == 'a' || input.charAt(0) == 'b' || input.charAt(0) == 'c' || input.charAt(0) == 'd'
                        || input.charAt(0) == 'e' || input.charAt(0) == 'f' || input.charAt(0) == 'g' || input.charAt(0) == 'h')
                && (input.charAt(1) == '1' || input.charAt(1) == '2' || input.charAt(1) == '3' || input.charAt(1) == '4'
                        || input.charAt(1) == '5' || input.charAt(1) == '6' || input.charAt(1) == '7' || input.charAt(1) == '8');
    }

    private static ChessPosition parsePos(String input) {
        input = input.toLowerCase();
        int row = Integer.parseInt(input.substring(1, 2));
        int col = 0;
        switch (input.charAt(0)) {
            case 'a' -> col = 1;
            case 'b' -> col = 2;
            case 'c' -> col = 3;
            case 'd' -> col = 4;
            case 'e' -> col = 5;
            case 'f' -> col = 6;
            case 'g' -> col = 7;
            case 'h' -> col = 8;
        }
        return new ChessPosition(row, col);
    }

    public static void updateGameObj(ChessGame game) {
        gameObj = game;
    }
}

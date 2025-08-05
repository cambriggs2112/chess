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

public class ClientMainFuncs {
    private static final String SERVER_URL = "http://localhost:8081";
    private static ArrayList<Integer> gameIDs = new ArrayList<Integer>();
    private static ArrayList<String> gameNames = new ArrayList<String>();

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

    private static void printBoard(ChessGame.TeamColor color, ChessGame game) {
        ChessBoard board = game.getBoard();
        if (color == ChessGame.TeamColor.WHITE) {
            System.out.println("\u2009     a\u2003 b\u2003 c\u2003 d\u2003 e\u2003 f\u2003 g\u2003 h");
            for (int row = 8; row >= 1; row--) {
                System.out.print("  " + row + " ");
                for (int col = 1; col <= 8; col++) {
                    printPiece(board, new ChessPosition(row, col));
                }
                System.out.println("\u001b[39m\u001b[49m " + row + " ");
            }
            System.out.println("\u2009     a\u2003 b\u2003 c\u2003 d\u2003 e\u2003 f\u2003 g\u2003 h");
        } else {
            System.out.println("\u2009     h\u2003 g\u2003 f\u2003 e\u2003 d\u2003 c\u2003 b\u2003 a");
            for (int row = 1; row <= 8; row++) {
                System.out.print("  " + row + " ");
                for (int col = 8; col >= 1; col--) {
                    printPiece(board, new ChessPosition(row, col));
                }
                System.out.println("\u001b[39m\u001b[49m " + row + " ");
            }
            System.out.println("\u2009     h\u2003 g\u2003 f\u2003 e\u2003 d\u2003 c\u2003 b\u2003 a");
        }
        System.out.println();
    }

    private static void printPiece(ChessBoard board, ChessPosition pos) {
        if ((pos.getRow() + pos.getColumn()) % 2 == 0) {
            System.out.print("\u001b[48;5;235m");
        } else {
            System.out.print("\u001b[48;5;242m");
        }
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

    private static void gameplayLoop(ChessGame.TeamColor color, String authToken, String username, int gameNum) {
        String gameName = gameNames.get(gameNum - 1);
        ChessGame game = new ChessGame();
        Scanner input = new Scanner(System.in);
        boolean drawBoard = true;
        // connect web socket here
        while (true) {
            if (drawBoard) {
                if (color == null) { // observer
                    printBoard(ChessGame.TeamColor.WHITE, game);
                } else {
                    printBoard(color, game);
                }
            }
            System.out.print("[" + username + "." + gameName + "] >>> ");
            ArrayList<String> arguments = parseInput(input.nextLine());
            if (arguments.isEmpty()) {
                drawBoard = false;
                continue;
            }
            if (arguments.getFirst().equalsIgnoreCase("help")) {
                drawBoard = false;
                System.out.println("\u001b[38;5;12m  highlight\u001b[39m - legal moves");
                if (color != null) {
                    System.out.println("\u001b[38;5;12m  move <SOURCE> <DESTINATION> <PROMOTION (optional)>\u001b[39m - make a move (e.g. f5 e4 QUEEN)");
                }
                System.out.println("\u001b[38;5;12m  redraw\u001b[39m - chess board");
                if (color != null) {
                    System.out.println("\u001b[38;5;12m  resign\u001b[39m - from game");
                }
                System.out.println("\u001b[38;5;12m  leave\u001b[39m - when you are done");
                System.out.println("\u001b[38;5;12m  help\u001b[39m - with possible commands");
                System.out.println();
            } else if (arguments.getFirst().equalsIgnoreCase("redraw")) {
                drawBoard = true;
            } else if (arguments.getFirst().equalsIgnoreCase("leave")) {
                break;
            } else if (arguments.getFirst().equalsIgnoreCase("move") && color != null) {

            } else if (arguments.getFirst().equalsIgnoreCase("resign") && color != null) {

            } else if (arguments.getFirst().equalsIgnoreCase("highlight")) {

            } else {
                System.out.println("\u001b[38;5;160m  Unknown command. Type Help for a list of commands.\u001b[39m");
            }
        }
    }
}

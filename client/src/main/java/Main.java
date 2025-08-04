import java.util.Scanner;
import java.util.ArrayList;
import ui.*;

public class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.println("♕ Welcome to 240 Chess. Type Help to get started. ♕");
        System.out.println();
        String authToken = null;
        String username = "";
        while (true) {
            if (authToken == null) {
                System.out.print("[] >>> ");
            } else {
                System.out.print("[" + username + "] >>> ");
            }
            ArrayList<String> arguments = ClientMainFuncs.parseInput(input.nextLine());
            if (arguments.isEmpty()) {
                continue;
            }
            if (authToken == null) {
                if (arguments.getFirst().equalsIgnoreCase("help")) {
                    System.out.println("\u001b[38;5;12m  register <USERNAME> <PASSWORD> <EMAIL>\u001b[39m - to create an account");
                    System.out.println("\u001b[38;5;12m  login <USERNAME> <PASSWORD>\u001b[39m - to play chess");
                    System.out.println("\u001b[38;5;12m  quit\u001b[39m - playing chess");
                    System.out.println("\u001b[38;5;12m  help\u001b[39m - with possible commands");
                    System.out.println();
                } else if (arguments.getFirst().equalsIgnoreCase("quit")) {
                    break;
                } else if (arguments.getFirst().equalsIgnoreCase("login")) {
                    authToken = ClientMainFuncs.login(arguments);
                    if (arguments.size() > 1) {
                        username = arguments.get(1);
                    }
                } else if (arguments.getFirst().equalsIgnoreCase("register")) {
                    authToken = ClientMainFuncs.register(arguments);
                    if (arguments.size() > 1) {
                        username = arguments.get(1);
                    }
                } else {
                    System.out.println("\u001b[38;5;160m  Unknown command. Type Help for a list of commands.\u001b[39m");
                }
            } else {
                if (arguments.getFirst().equalsIgnoreCase("help")) {
                    System.out.println("\u001b[38;5;12m  create <NAME>\u001b[39m - a game");
                    System.out.println("\u001b[38;5;12m  list\u001b[39m - games");
                    System.out.println("\u001b[38;5;12m  join <ID> [WHITE|BLACK]\u001b[39m - a game");
                    System.out.println("\u001b[38;5;12m  observe <ID>\u001b[39m - a game");
                    System.out.println("\u001b[38;5;12m  logout\u001b[39m - when you are done");
                    System.out.println("\u001b[38;5;12m  quit\u001b[39m - playing chess");
                    System.out.println("\u001b[38;5;12m  help\u001b[39m - with possible commands");
                    System.out.println();
                } else if (arguments.getFirst().equalsIgnoreCase("logout")) {
                    ClientMainFuncs.logout(authToken, true);
                    authToken = null;
                    username = "";
                } else if (arguments.getFirst().equalsIgnoreCase("create")) {
                    ClientMainFuncs.createGame(arguments, authToken);
                } else if (arguments.getFirst().equalsIgnoreCase("list")) {
                    ClientMainFuncs.listGames(authToken);
                } else if (arguments.getFirst().equalsIgnoreCase("join")) {
                    ClientMainFuncs.joinGame(arguments, authToken, username);
                } else if (arguments.getFirst().equalsIgnoreCase("observe")) {
                    ClientMainFuncs.observeGame(arguments, authToken, username);
                } else if (arguments.getFirst().equalsIgnoreCase("quit")) {
                    ClientMainFuncs.logout(authToken, false);
                    break;
                } else {
                    System.out.println("\u001b[38;5;160m  Unknown command. Type Help for a list of commands.\u001b[39m");
                }
            }
        }
    }
}
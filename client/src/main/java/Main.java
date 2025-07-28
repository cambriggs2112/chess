import java.util.Scanner;
import java.util.ArrayList;
import ui.*;

public class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.println("♕ Welcome to 240 Chess. Type Help to get started. ♕");
        System.out.println();
        String authToken = null;
        while (true) {
            if (authToken == null) {
                System.out.print("[LOGGED_OUT] >>> ");
            } else {
                System.out.print("[LOGGED_IN] >>> ");
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
                } else if (arguments.getFirst().equalsIgnoreCase("register")) {
                    authToken = ClientMainFuncs.register(arguments);
                } else {
                    System.out.println("\u001b[38;5;160m  Unknown command. Type Help for a list of commands.\u001b[39m");
                }
            } else {
                if (arguments.getFirst().equalsIgnoreCase("help")) {
                    System.out.println("\u001b[38;5;12m  register <USERNAME> <PASSWORD> <EMAIL>\u001b[39m - to create an account");
                    System.out.println("\u001b[38;5;12m  login <USERNAME> <PASSWORD>\u001b[39m - to play chess");
                    System.out.println("\u001b[38;5;12m  quit\u001b[39m - playing chess");
                    System.out.println("\u001b[38;5;12m  help\u001b[39m - with possible commands");
                    System.out.println();
                } else if (arguments.getFirst().equalsIgnoreCase("quit")) {
                    break;
                } else {
                    System.out.println("\u001b[38;5;160m  Unknown command. Type Help for a list of commands.\u001b[39m");
                }
            }
        }
    }
}
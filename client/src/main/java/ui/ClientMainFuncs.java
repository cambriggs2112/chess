package ui;

import java.util.ArrayList;
import model.*;

public class ClientMainFuncs {
    private static final String SERVER_URL = "http://localhost:8081";

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
        for (int i = 0; i < result.size(); i++) {
            if (result.get(i).isEmpty()) {
                result.remove(i);
                i--;
            }
        }
        return result;
    }

    public static String register(ArrayList<String> arguments) {
        if (arguments.size() != 4) {
            System.out.println("\u001b[38;5;160m  Usage: register <USERNAME> <PASSWORD> <EMAIL>\u001b[39m");
            return null;
        }
        String username = arguments.get(1);
        String password = arguments.get(2);
        String email = arguments.get(3);
        try {
            ServerFacade sf = new ServerFacade(SERVER_URL);
            RegisterResult res = sf.register(new RegisterRequest(username, password, email));
            System.out.println("  Logged in as " + res.username());
            return res.authToken();
        } catch (ServiceException e) {
            System.out.println("\u001b[38;5;160m  " + e + "\u001b[39m");
            return null;
        }
    }
}

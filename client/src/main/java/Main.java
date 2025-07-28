import java.util.Scanner;
import java.util.ArrayList;
import ui.*;

public class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        ArrayList<String> arguments = new ArrayList<String>();
        System.out.println("♕ Welcome to 240 Chess. Type Help to get started. ♕");
        while (arguments.isEmpty() || !arguments.getFirst().equalsIgnoreCase("quit")) {
            System.out.println();
            System.out.print("[LOGGED_OUT] >>> ");
            arguments.clear();
            String line = input.nextLine();
            for (int i = 0; i < line.length(); i++) {
                if (line.charAt(i) == ' ') {
                    arguments.add(line.substring(0, i));
                    line = line.substring(i + 1);
                    i = -1;
                }
            }
            arguments.add(line);
        }
    }
}
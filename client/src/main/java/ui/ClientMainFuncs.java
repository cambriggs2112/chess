package ui;

import java.util.ArrayList;

public class ClientMainFuncs {
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
}

package dataaccess;

public class EscapeCorrection {
    public static String escapeApostrophes(String str) {
        String result = str;
        for (int i = 0; i < result.length(); i++) {
            if (result.charAt(i) == '\'') {
                result = result.substring(0, i) + "'" + result.substring(i);
                i++;
            }
        }
        return result;
    }
}

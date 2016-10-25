package pixyel_backend.database;

import java.util.List;

public class SqlUtils {

    public static String escapeString(String str) {
        if (str == null) {
            return null;
        }

        if (str.replaceAll("[a-zA-Z0-9_!@#$%^&*()-=+~.;:,\\Q[\\E\\Q]\\E<>{}\\/? ]", "").length() < 1) {
            return str;
        }

        String clean_string = str;
        clean_string = clean_string.replaceAll("\\\\", "\\\\\\\\");
        clean_string = clean_string.replaceAll("'", "\\\\'");
        clean_string = clean_string.replaceAll("\\\"", "\\\\\"");
        clean_string = clean_string.replaceAll("\\n", "\\\\n");
        clean_string = clean_string.replaceAll("\\r", "\\\\r");
        clean_string = clean_string.replaceAll("\\t", "\\\\t");
        clean_string = clean_string.replaceAll("\\00", "\\\\0");

        if (clean_string.replaceAll("[a-zA-Z0-9_!@#$%^&*()-=+~.;:,\\Q[\\E\\Q]\\E<>{}\\/?\\\\\"' ]", "").length() < 1) {
            return clean_string;
        } else {
            return null;
        }
    }

    public static String listToSqlINString(List input) {
        StringBuilder output = new StringBuilder();
        final String highcomma = "'";
        final String highcommacomma = "',";
        input.forEach((Object obj) -> {
            output.append(highcomma);
            output.append(obj);
            output.append(highcommacomma);
        });
        output.deleteCharAt(output.length() - 1);
        return output.toString();
    }
}

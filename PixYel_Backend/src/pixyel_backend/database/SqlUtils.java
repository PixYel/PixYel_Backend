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
		}
		return null;
	}
    public static String listToSqlINString(List input){
        StringBuffer output = new StringBuffer();
        for(Object obj:input){
            output.append("'");
            output.append(obj);
            output.append("',");
        }
        output.deleteCharAt(output.length()-1);
        return output.toString();
    }
}

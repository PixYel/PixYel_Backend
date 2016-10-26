/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Yannick
 */
public class Log {

    static int maxLengthOfClassName = 0;

    public static void logInfo(String logMessage, Object clasS) {
        String[] lines = logMessage.split("\\r?\\n");
        String toPrint = getClassNameWithDate(clasS) + "INFO:    ";
        for (String line : lines) {
            System.out.println(toPrint + line);
        }
    }

    public static void logError(String logMessage, Object clasS) {
        String[] lines = logMessage.split("\\r?\\n");
        String toPrint = getClassNameWithDate(clasS) + "ERROR:   ";
        for (String line : lines) {
            System.err.println(toPrint + line);
        }
    }

    public static void logWarning(String logMessage, Object clasS) {
        String[] lines = logMessage.split("\\r?\\n");
        String toPrint = getClassNameWithDate(clasS) + "WARNING: ";
        for (String line : lines) {
            System.out.println(toPrint + line);
        }
    }

    /**
     *
     * @param clasS
     * @param color 0 = black, 1 = red
     */
    private static String getClassNameWithDate(Object clasS) {
        String result = "";
        int tempLength;
        String className = clasS.getClass().getTypeName();
        className = className.substring(className.lastIndexOf(".") + 1);
        if (className.contains("$")) {
            className = className.substring(0, className.lastIndexOf("$"));
        }
        if ((tempLength = className.length()) > maxLengthOfClassName) {
            maxLengthOfClassName = tempLength;
        }
        if (maxLengthOfClassName % 2 == 0) {
            for (int i = 0; i < (maxLengthOfClassName - tempLength) / 2; i++) {
                result += (" ");
            }
            result += ("[" + className + "]");
            for (int i = 0; i < (maxLengthOfClassName - tempLength) / 2; i++) {
                result += (" ");
            }
        } else {
            for (int i = 0; i < ((maxLengthOfClassName - tempLength) / 2) - 1; i++) {
                result += (" ");
            }
            result += ("[" + className + "]");
            for (int i = 0; i < (maxLengthOfClassName - tempLength) / 2; i++) {
                result += (" ");
            }
        }
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        result += " [" + dateFormat.format(new Date()) + "] ";
        return result;
    }
}

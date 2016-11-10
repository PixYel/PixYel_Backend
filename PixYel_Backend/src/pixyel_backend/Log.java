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

    private static int maxLengthOfClassName = 0;

    /**
     * It is logging the logMessage as a Info
     *
     * @param logMessage The message to be logged
     * @param clasS The classname of the class of the message
     */
    public static void logInfo(String logMessage, Object clasS) {
        String[] lines = logMessage.split("\\r?\\n");
        String toPrint = getClassNameWithDate(clasS) + "INFO:    ";
        for (String line : lines) {
            System.out.println(toPrint + line);
        }
    }

    /**
     * It is logging the logMessage as a error
     *
     * @param logMessage The message to be logged
     * @param clasS The classname of the class of the message
     */
    public static void logError(String logMessage, Object clasS) {
        String[] lines = logMessage.split("\\r?\\n");
        String toPrint = getClassNameWithDate(clasS) + "ERROR:   ";
        for (String line : lines) {
            System.err.println(toPrint + line);
        }
    }

    /**
     * It is logging the logMessage as a warning
     *
     * @param logMessage The message to be logged
     * @param clasS The classname of the class of the message
     */
    public static void logWarning(String logMessage, Object clasS) {
        String[] lines = logMessage.split("\\r?\\n");
        String toPrint = getClassNameWithDate(clasS) + "WARNING: ";
        for (String line : lines) {
            System.out.println(toPrint + line);
        }
    }

    /**
     * If the {@link Main.DEBUG} parameter is set to true, it is logging the
     * logMessage
     *
     * @param logMessage The message to be logged
     * @param clasS The classname of the class of the message
     */
    public static void logDebug(String logMessage, Object clasS) {
        if (Main.DEBUG) {
            String[] lines = logMessage.split("\\r?\\n");
            String toPrint = getClassNameWithDate(clasS) + "DEBUG:   ";
            for (String line : lines) {
                System.out.println(toPrint + line);
            }
        }
    }

    /**
     * Returns the Classname with the Date, this String can only grow, so The
     * text should be formatted well
     *
     * @param clasS The class to extract the classname from
     */
    private static String getClassNameWithDate(Object clasS) {
        String result = "";
        int currentLengthOfClassName;
        String className = clasS.getClass().getTypeName();
        className = className.substring(className.lastIndexOf(".") + 1);
        if (className.contains("$")) {
            className = className.substring(0, className.lastIndexOf("$"));
        }
        if ((currentLengthOfClassName = className.length()) > maxLengthOfClassName) {
            maxLengthOfClassName = currentLengthOfClassName;
        }

        for (int i = 0; i < (maxLengthOfClassName - currentLengthOfClassName) / 2; i++) {
            result += (" ");
        }
        result += ("[" + className + "]");
        if ((maxLengthOfClassName - currentLengthOfClassName) % 2 == 0) {
            for (int i = 0; i < (maxLengthOfClassName - currentLengthOfClassName) / 2; i++) {
                result += (" ");
            }
        } else {
            for (int i = 0; i < ((maxLengthOfClassName - currentLengthOfClassName) / 2) + 1; i++) {
                result += (" ");
            }
        }

        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        result += " [" + dateFormat.format(new Date()) + "] ";
        return result;
    }
}

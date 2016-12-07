/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import pixyel_backend.userinterface.UIs.DesktopUI.ConsoleWindow;

/**
 *
 * @author Yannick
 */
public class Log {

    private static int maxLengthOfClassName = 0;

    /**
     * It is logging the logMessage as a Info
     *
     * @param <T> The Type of the object (The classname)
     * @param logMessage The message to be logged
     * @param clasS The classname of the class of the message
     */
    public static <T> void logInfo(String logMessage, Class<T> clasS) {
        String[] lines = logMessage.split("\\r?\\n");
        String toPrint = getClassNameWithDate(clasS) + "INFO:    ";
        boolean first = true;
        for (String line : lines) {
            if (first) {
                System.out.println(toPrint + line);
                first = false;
            } else {
                System.out.println("         " + line);
            }
        }
        windows.forEach((ConsoleWindow window) -> window.addInfo(logMessage, getClassName(clasS)));
    }

    /**
     * It is logging the logMessage as a error
     *
     * @param <T> The Type of the object (The classname)
     * @param logMessage The message to be logged
     * @param clasS The classname of the class of the message
     */
    public static <T> void logError(String logMessage, Class<T> clasS) {
        String[] lines = logMessage.split("\\r?\\n");
        String toPrint = getClassNameWithDate(clasS) + "ERROR:   ";
        boolean first = true;
        for (String line : lines) {
            if (first) {
                System.out.println(toPrint + line);
                first = false;
            } else {
                System.out.println("         " + line);
            }
        }
        windows.forEach((ConsoleWindow window) -> window.addError(logMessage, getClassName(clasS)));
    }

    /**
     * It is logging the logMessage as a warning
     *
     * @param <T> The Type of the object (The classname)
     * @param logMessage The message to be logged
     * @param clasS The classname of the class of the message
     */
    public static <T> void logWarning(String logMessage, Class<T> clasS) {
        String[] lines = logMessage.split("\\r?\\n");
        String toPrint = getClassNameWithDate(clasS) + "WARNING: ";
        boolean first = true;
        for (String line : lines) {
            if (first) {
                System.out.println(toPrint + line);
                first = false;
            } else {
                System.out.println("         " + line);
            }
        }
        windows.forEach((ConsoleWindow window) -> window.addWarning(logMessage, getClassName(clasS)));
    }

    /**
     * If the {@link Main.DEBUG} parameter is set to true, it is logging the
     * logMessage
     *
     * @param <T> The Type of the object (The classname)
     * @param logMessage The message to be logged
     * @param clasS The classname of the class of the message
     */
    public static <T> void logDebug(String logMessage, Class<T> clasS) {
        if (Main.DEBUG) {
            String[] lines = logMessage.split("\\r?\\n");
            String toPrint = getClassNameWithDate(clasS) + "DEBUG:   ";
            boolean first = true;
            for (String line : lines) {
                if (first) {
                    System.out.println(toPrint + line);
                    first = false;
                } else {
                    System.out.println("         " + line);
                }
            }
            windows.forEach((ConsoleWindow window) -> window.addDebug(logMessage, getClassName(clasS)));
        }
    }

    /**
     * Returns the Classname with the Date, this String can only grow, so The
     * text should be formatted well
     *
     * @param clasS The class to extract the classname from
     */
    private static <T> String getClassNameWithDate(Class<T> clasS) {
        String result = "";
        int currentLengthOfClassName;
        String className = getClassName(clasS);
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

    public static String getClassName(Class<?> T) {
        String className = T.getTypeName();
        className = className.substring(className.lastIndexOf(".") + 1);
        if (className.contains("$")) {
            className = className.substring(0, className.lastIndexOf("$"));
        }
        return className;
    }

    private static ArrayList<ConsoleWindow> windows = new ArrayList<>();

    public static void addConsoleWindow(ConsoleWindow window) {
        windows.add(window);
    }

    public static void removeConsoleWindow(ConsoleWindow window) {
        windows.remove(window);
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend;

import com.vaadin.ui.HorizontalLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import pixyel_backend.connection.Utils;
import pixyel_backend.userinterface.UIs.DesktopUI.apps.ConsoleWindow;

/**
 *
 * @author Yannick
 */
public class Log {

    private static int maxLengthOfClassName = 0;
    private static final ArrayBlockingQueue<LogMessage> maxQueue = new ArrayBlockingQueue<>(250);
    private static File logFile;

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
                writeToFile(toPrint + line);
                first = false;
            } else {
                System.out.println("         " + line);
                writeToFile("         " + line);
            }
        }
        windows.forEach((ConsoleWindow window) -> window.addInfo(logMessage, getClassName(clasS), new Date()));
        if (!(maxQueue.size() < 250)) {
            maxQueue.poll();
        }
        maxQueue.add(new LogMessage(0, logMessage, clasS));

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
                writeToFile(toPrint + line);
                first = false;
            } else {
                System.out.println("         " + line);
                writeToFile("         " + line);
            }
        }
        windows.forEach((ConsoleWindow window) -> window.addError(logMessage, getClassName(clasS), new Date()));
        if (!(maxQueue.size() < 250)) {
            maxQueue.poll();
        }
        maxQueue.add(new LogMessage(2, logMessage, clasS));
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
                writeToFile(toPrint + line);
                first = false;
            } else {
                System.out.println("         " + line);
                writeToFile("         " + line);
            }
        }
        windows.forEach((ConsoleWindow window) -> window.addWarning(logMessage, getClassName(clasS), new Date()));
        if (!(maxQueue.size() < 250)) {
            maxQueue.poll();
        }
        maxQueue.add(new LogMessage(1, logMessage, clasS));
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
                    writeToFile(toPrint + line);
                    first = false;
                } else {
                    System.out.println("         " + line);
                    writeToFile("         " + line);
                }
            }
            windows.forEach((ConsoleWindow window) -> window.addDebug(logMessage, getClassName(clasS), new Date()));
            if (!(maxQueue.size() < 250)) {
                maxQueue.poll();
            }
            maxQueue.add(new LogMessage(3, logMessage, clasS));
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
        addAll(window);
    }

    public static void removeConsoleWindow(ConsoleWindow window) {
        windows.remove(window);
    }

    public static void addAll(ConsoleWindow window) {
        maxQueue.forEach((LogMessage log) -> {
            switch (log.messageType) {
                case 0:
                    window.addInfo(log.message, log.type, log.date);
                    break;
                case 1:
                    window.addWarning(log.message, log.type, log.date);
                    break;
                case 2:
                    window.addError(log.message, log.type, log.date);
                    break;
                case 3:
                    window.addDebug(log.message, log.type, log.date);
                    break;
            }
        });
    }

    public static class LogMessage {

        //0 = info, 1 = warning, 2 = error, 3 = debug
        int messageType;

        String message;

        String type;

        Date date;

        public LogMessage(int messageType, String message, Class<?> type) {
            this.messageType = messageType;
            this.message = message;
            this.type = getClassName(type);
            this.date = new Date();
        }

    }

    public static void writeToFile(String toWrite) {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String fileName = "pixyel_log_" + dateFormat.format(new Date()) + ".txt";
        File dir = new File(System.getProperty("user.home") + "\\Desktop\\PixYel\\Log");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File logFile = new File(dir.getAbsolutePath() + "\\" + fileName);
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException ex) {
                System.err.println();
            }
        }
        String filePath = System.getProperty("user.home") + "\\Desktop\\PixYel\\Log\\" + fileName;
        toWrite += " \n";
        try{
            Files.write(Paths.get(filePath), toWrite.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    public static void getRessource(String name) {
//        try {
//            File dir = new File(System.getProperty("user.dir"));
//            if (file.exists()) {
//                return file;
//            } else {
//                //throw new RessourceNotFoundException("Could not find ressource: " + file.getPath().replaceAll("%20", " "));
//            }
//        } catch (Exception e) {
//            //throw new RessourceNotFoundException("Could not find ressource: " + name);
//        }
    }
}

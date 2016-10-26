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
        String toPrint = getClassNameWithDate(clasS);
        toPrint += "INFO:    " + logMessage;
        System.out.println(toPrint);
    }

    public static void logError(String logMessage, Object clasS) {
        String toPrint = getClassNameWithDate(clasS);
        toPrint += "ERROR:   " + logMessage;
        System.err.println(toPrint);
    }

    public static void logWarning(String logMessage, Object clasS) {
        String toPrint = getClassNameWithDate(clasS);
        toPrint += "WARNING: " + logMessage;
        System.out.println(toPrint);
    }

    /**
     *
     * @param clasS
     * @param color 0 = black, 1 = red
     */
    private static String getClassNameWithDate(Object clasS) {
        String result = "";
        int tempLength;
        if ((tempLength = clasS.getClass().getTypeName().length()) > maxLengthOfClassName) {
            maxLengthOfClassName = tempLength;
        }
        if (maxLengthOfClassName % 2 == 0) {
            for (int i = 0; i < (maxLengthOfClassName - tempLength) / 2; i++) {
                result += (" ");
            }
            result += ("[" + clasS.getClass().getTypeName() + "]");
            for (int i = 0; i < (maxLengthOfClassName - tempLength) / 2; i++) {
                result += (" ");
            }
        } else {
            for (int i = 0; i < ((maxLengthOfClassName - tempLength) / 2) - 1; i++) {
                result += (" ");
            }
            result += ("[" + clasS.getClass().getTypeName() + "]");
            for (int i = 0; i < (maxLengthOfClassName - tempLength) / 2; i++) {
                result += (" ");
            }
        }
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        result += " [" + dateFormat.format(new Date()) + "] ";
        return result;
    }
}

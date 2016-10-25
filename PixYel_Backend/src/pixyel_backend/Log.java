/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Yannick
 */
public class Log {

    private final static Logger LOG = Logger.getLogger(Logger.class.getName());

    public static void logInfo(String logMessage) {
        LOG.log(Level.WARNING, logMessage);
    }
    
    public static void logError(String logMessage){
        LOG.log(Level.SEVERE,logMessage);
    }

    public static void logWarning(String logMessage) {
        LOG.log(Level.WARNING, logMessage);
    }
}

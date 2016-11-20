/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend;

import pixyel_backend.connection.Connection;
import pixyel_backend.database.DatabaseCleanUpService;

/**
 *
 * @author Josua Frank
 */
public class Main {

    /**
     * Setting for the Debug mode
     */
    public static boolean DEBUG = true;
    
    public static void main(String[] args) {
        DatabaseCleanUpService.start();
        Connection.start();
        //UI.start();
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend;

import pixyel_backend.connection.Connection;
import pixyel_backend.database.DatabaseCleanUpService;
import pixyel_backend.userinterface.Userinterface;

/**
 *
 * @author Josua Frank
 */
public class Main {

    /**
     * Setting for the Debug mode, affects the Log and Vaadin on the Jetty
     * server
     */
    public static final boolean DEBUG = false;

    public static void main(String[] args) {
        Userinterface.start();
        Userinterface.onStarted(() -> {
            DatabaseCleanUpService.start();
            Connection.start();
        });
    }
}

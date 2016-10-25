/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend.connection;

import pixyel_backend.Log;
import pixyel_backend.database.objects.User;
import pixyel_backend.xml.XML;

/**
 *
 * @author i01frajos445
 */
public class Command {

    public static void onCommandReceived(Client connection, User userdata, XML xml) {
        Log.logInfo("Command received: \n" + xml);
        try {
            switch (xml.getName()) {
                case "echo":
                    connection.sendToClient(XML.createNewXML("echo").toXMLString());
                    break;
            }
        } catch (Exception e) {
            Log.logError("Could not execute command: " + xml.getName() + ": " + e);
        }

    }

}

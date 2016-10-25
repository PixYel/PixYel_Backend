/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend.connection;

import pixyel_backend.database.objects.User;
import pixyel_backend.xml.XML;

/**
 *
 * @author i01frajos445
 */
public class Command {

    public static void onCommandReceived(Client connection, User userdata, XML xml) {
        System.out.println("Command received: \n" + xml);
        try {
            switch (xml.getName()) {
                case "login":

                    break;
                case "echo":
                    connection.sendToClient(XML.createNewXML("echo").toXMLString());
                    break;
            }
        } catch (Exception e) {
            System.err.println("Could not execute command: " + xml.getName() + ": " + e);
        }

    }

}

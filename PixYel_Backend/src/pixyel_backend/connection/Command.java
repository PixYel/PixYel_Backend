/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend.connection;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import pixyel_backend.Log;
import pixyel_backend.database.exceptions.UserNotFoundException;
import pixyel_backend.database.objects.User;
import pixyel_backend.xml.XML;

/**
 *
 * @author i01frajos445
 */
public class Command {

    public static void onCommandReceived(Client connection, XML xml) {
        Log.logInfo("Command received: \n" + xml, Command.class);
        try {
            switch (xml.getName()) {
                /*
                Is for debugging reasons only
                */
                case "echo":
                    DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                    String date = " " + dateFormat.format(new Date()) + " ";
                    connection.sendToClient(XML.createNewXML("echo_zurueck").addAttribute("Date", date).toXMLString());
                    break;
                    /*
                    The client wants to disconnect
                    */
                case "disconnect":
                    connection.disconnect(true);
                    break;
                    /*
                    The client wants to connect
                    */
                case "login":
                    try {
                        connection.userdata = User.getUser(xml.getFirstChild("storeid").getContent());
                    } catch (UserNotFoundException ex) {
                        connection.userdata = User.addNewUser(xml.getFirstChild("storeid").getContent());
                    }
                    connection.userdata.setPublicKey(xml.getFirstChild("publickey").getContent());
                    break;
            }
        } catch (Exception e) {
            Log.logError("Could not execute command: " + xml.getName() + ": " + e, Command.class);
        }

    }

}

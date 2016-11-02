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

    public static void onCommandReceived(Client client, XML xml) {
        Log.logInfo("Command from " + client.getName() + " received: \n" + xml.toStringGraph(), Command.class);
        try {
            switch (xml.getName()) {
                /*
                Is for debugging reasons only
                 */
                case "echo":
                    DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                    String date = " " + dateFormat.format(new Date()) + " ";
                    client.sendToClient(XML.createNewXML("echo_zurueck").addAttribute("Date", date).toString());
                    break;
                /*
                    The client wants to disconnect
                 */
                case "disconnect":
                    client.disconnect(true);
                    break;
                /*
                    The client wants to connect
                 */
                case "login":
                    try {
                        client.userdata = User.getUser(xml.getFirstChild("storeid").getContent());
                    } catch (UserNotFoundException ex) {
                        client.userdata = User.addNewUser(xml.getFirstChild("storeid").getContent());
                    }
                    client.userdata.setPublicKey(xml.getFirstChild("publickey").getContent());
                    client.sendToClient(XML.createNewXML("loginsuccessful").toString());
                    break;
                /*
                    The client replies to the alive message from the server (to avoid dead clients)
                 */
                case "alive":
                    client.checkClientAlive(true);
                    break;
            }
        } catch (Exception e) {
            Log.logWarning("Could not execute command: " + xml.getName() + ": " + e, Command.class);
        }

    }

}

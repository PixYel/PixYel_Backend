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
import pixyel_backend.database.BackendFunctions;
import pixyel_backend.database.exceptions.UserNotFoundException;
import pixyel_backend.database.objects.User;
import pixyel_backend.xml.XML;

/**
 *
 * @author i01frajos445
 */
public class Command {

    public static void onCommandReceived(Client client, XML xml) {
        BackendFunctions backendFunctions = client.getBackendFunctions();
        //Log.logInfo("Command from " + client.getName() + " received: \n" + xml.toStringGraph(), Command.class);
        try {
            switch (xml.getName()) {
                /*
                    Is for debugging reasons only
                 */
                case "echo":
                    DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                    String date = " " + dateFormat.format(new Date()) + " ";
                    Log.logInfo("Sending echo to client " + client.getName(), Command.class);
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
                        try {
                            client.setUserdata(User.getUser(xml.getFirstChild("storeid").getContent()));
                        } catch (UserNotFoundException ex) {
                            client.setUserdata(User.addNewUser(xml.getFirstChild("storeid").getContent()));
                        }
                        client.getUserdata().setPublicKey(xml.getFirstChild("publickey").getContent());
                        client.sendToClient(XML.createNewXML("loginsuccessful").toString());
                        Log.logInfo("Successfully logged " + client.getName() + " in", Command.class);
                    } catch (Exception e) {
                        client.sendToClient(XML.createNewXML("loginunsuccessful").toString());
                        Log.logWarning("Failed to log " + client.getName() + " in: " + e, Command.class);
                    }
                    break;
                /*
                    The client replies to the alive message from the server (to avoid dead clients)
                 */
                case "alive":
                    client.checkClientAlive(true);
                    break;

                case "fotorequest":
                    int xCordinate = Integer.valueOf(xml.getFirstChild("xcodinate").getContent());
                    int yCordinate = Integer.valueOf(xml.getFirstChild("ycodinate").getContent());
                    XML picturesXML = backendFunctions.getPictures(xCordinate, yCordinate);
                    client.sendToClient(picturesXML.toString());
                    break;
                case "commentforpicture":
                    int pictureId = Integer.valueOf(xml.getAttribute("id"));
                    XML allComments = backendFunctions.getCommentsForPicutre(pictureId);
                    client.sendToClient(allComments.toString());
                    break;
                case "flagpicture":
                    pictureId = Integer.valueOf(xml.getAttribute("id"));
                    backendFunctions.flagPicture(pictureId);
                    break;
                case "flagcomment":
                    int commentId = Integer.valueOf(xml.getAttribute("id"));
                    backendFunctions.flagComment(commentId);
                    break;
                case "addnewcomment":
                    String text = xml.getFirstChild("text").getContent();
                    int refersToPicuture = Integer.valueOf(xml.getAttribute("pictureId"));
                    backendFunctions.addNewComment(text, refersToPicuture);
                    break;
                case "uploadpicture":
                    String picturedata = xml.getFirstChild("data").getContent();
                    backendFunctions.uploadPicture(picturedata);
                    break;

            }
        } catch (Exception e) {
            Log.logWarning("Could not execute command: " + xml.getName() + ": " + e, Command.class);
        }

    }

}

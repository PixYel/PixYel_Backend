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
            switch ((xml = xml.getFirstChild()).getName()) {

                case "getItemList":
                    XML location = xml.getFirstChild();
                    int longt = Integer.valueOf(location.getFirstChild("long").getContent());
                    int lat = Integer.valueOf(location.getFirstChild("lat").getContent());

                    break;
                case "getItem":
                    int id = Integer.valueOf(xml.getFirstChild().getContent());

                    break;
                case "getItemStats":
                    int id1 = Integer.valueOf(xml.getFirstChild().getContent());

                    break;
                case "login":
                    try {
                        client.setUserdata(User.getUser(xml.getFirstChild("storeid").getContent()));
                    } catch (UserNotFoundException ex) {
                        client.setUserdata(User.addNewUser(xml.getFirstChild("storeid").getContent()));
                    }
                    try {
                        client.getUserdata().setPublicKey(xml.getFirstChild("publickey").getContent());
                        client.sendToClient(XML.createNewXML("loginsuccessful").toString());
                        Log.logInfo("Successfully logged " + client.getName() + " in", Command.class);
                    } catch (Exception e) {
                        client.sendToClient(XML.createNewXML("loginunsuccessful").toString());
                        Log.logWarning("Failed to log " + client.getName() + " in: " + e, Command.class);
                    }
                    break;
                case "echo":
                    DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                    String date = " " + dateFormat.format(new Date()) + " ";
                    Log.logInfo("Sending echo to client " + client.getName(), Command.class);
                    client.sendToClient(XML.createNewXML("echo_zurueck").addAttribute("Date", date).toString());
                    break;
                case "vote":
                    int id2 = Integer.valueOf(xml.getFirstChild("id").getContent());
                    boolean upvote = Boolean.valueOf(xml.getFirstChild("upvote").getContent());

                    break;
                case "upload":
                    String data = xml.getFirstChild("xml").getContent();
                    int longt1 = Integer.valueOf(xml.getFirstChild("long").getContent());
                    int lat1 = Integer.valueOf(xml.getFirstChild("lat").getContent());

                    break;
                case "flag":
                    int id3 = Integer.valueOf(xml.getFirstChild("id").getContent());

                    break;
                case "getComments":
                    int id4 = Integer.valueOf(xml.getFirstChild("id").getContent());

                    break;
                case "disconnect":
                    client.disconnect(true);
                    break;
                /*
                    The client replies to the alive message from the server (to avoid dead clients)
                 */
                case "alive":
                    client.checkClientAlive(true);
                    break;
                /*
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
                 */
            }
        } catch (Exception e) {
            Log.logWarning("Could not execute command: " + xml.getName() + ": " + e, Command.class);
        }

    }

}

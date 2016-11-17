/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend.connection;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import pixyel_backend.Log;
import pixyel_backend.database.BackendFunctions;
import pixyel_backend.database.exceptions.UserNotFoundException;
import pixyel_backend.database.objects.Comment;
import pixyel_backend.database.objects.User;
import pixyel_backend.xml.XML;

/**
 *
 * @author i01frajos445
 */
public class Command {

    /**
     * This method is being called when a command is arrived
     *
     * @param client The Client
     * @param xml
     */
    public static void onCommandReceived(Client client, XML xml) {
        BackendFunctions backendFunctions = client.getBackendFunctions();
        Log.logDebug("Command from " + client.getName() + " received: \n" + xml.toStringGraph(), Command.class);
        try {
            if (!(xml.getName().equals("request") || xml.getName().equals("backendUI"))) {
                Log.logWarning("Command from " + client.getName() + " does not start with \"request\" or \"backendUI\": " + xml.getName(), Command.class);
            }
            if (xml.getName().equals("request")) {
                switch ((xml = xml.getFirstChild()).getName()) {//Cuts off the "request"

                    case "getItemList":
                        XML location = xml.getFirstChild();
                        int longt = Integer.valueOf(location.getFirstChild("long").getContent());
                        int lat = Integer.valueOf(location.getFirstChild("lat").getContent());
                        // XML picturesXML = backendFunctions.getPicturesList(longt, lat);
                        // client.sendToClient(picturesXML);
                        break;
                    case "getItem":
                        int id = Integer.valueOf(xml.getFirstChild().getContent());
                        //backendFunctions.getPicturesData(listAsString)
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
                            client.sendToClient(XML.createNewXML("loginsuccessful"));
                            Log.logInfo("Successfully logged " + client.getName() + " in", Command.class);
                        } catch (Exception e) {
                            client.sendToClient(XML.createNewXML("loginunsuccessful"));
                            Log.logWarning("Failed to log " + client.getName() + " in: " + e, Command.class);
                        }
                        break;
                    case "echo":
                        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                        String date = " " + dateFormat.format(new Date()) + " ";
                        Log.logInfo("Sending echo to client " + client.getName(), Command.class);
                        client.sendToClient(XML.createNewXML("echo_zurueck").addAttribute("Date", date));
                        break;
                    case "vote":
                        int id2 = Integer.valueOf(xml.getFirstChild("id").getContent());
                        boolean upvote = Boolean.valueOf(xml.getFirstChild("upvote").getContent());
                        //backendFunctions.
                        break;
                    case "upload":
                        String data = xml.getFirstChild("data").getContent();
                        int longt1 = Integer.valueOf(xml.getFirstChild("long").getContent());
                        int lat1 = Integer.valueOf(xml.getFirstChild("lat").getContent());
                        client.userdata.uploadPicture(data, longt1, lat1);
                        Log.logInfo("Successfully uploaded image by "+client.getName(), Command.class);
                        break;
                    case "flagItem":
                        int id3 = Integer.valueOf(xml.getFirstChild("id").getContent());
                        client.userdata.flagPicture(id3);
                        break;
                    case "flagComment":
                        int commentId = Integer.valueOf(xml.getFirstChild("id").getContent());
                        client.userdata.flagComment(commentId);
                        break;
                    case "getComments":
                        int id4 = Integer.valueOf(xml.getFirstChild("id").getContent());
                        List<Comment> allComments = BackendFunctions.getCommentsForPicutre(id4);
                        // client.sendToClient(allComments);
                        break;
                    case "addComment":
                        int id5 = Integer.valueOf(xml.getFirstChild("id").getContent());
                        String content = xml.getFirstChild("content").getContent();
                        client.userdata.addNewComment(content, id5);
                        break;
                    case "disconnect":
                        client.disconnect(true);
                        break;
                    case "alive":
                        client.checkClientAlive(true);
                        break;
                }
            } else if (xml.getName().equals("backendUI")) {
                switch ((xml = xml.getFirstChild()).getName()) {//Cuts off the "backendUI"
                    case "login":
                        String username = xml.getFirstChild("username").getContent();
                        String password = xml.getFirstChild("password").getContent();
                        String publicKey = xml.getFirstChild("publickey").getContent();
                        if (UI.login(client, username, password)) {
                            try {
                                client.setUserdata(User.getUser(username));
                            } catch (UserNotFoundException ex) {
                                client.setUserdata(User.addNewUser(username));
                            }
                            try {
                                client.getUserdata().setPublicKey(publicKey);
                                client.sendToClient(XML.createNewXML("loginsuccessful"));
                                Log.logInfo("UI successfully accepted", Command.class);
                            } catch (Exception e) {
                                client.sendToClient(XML.createNewXML("loginunsuccessful"));
                                Log.logWarning("Failed to accept UI: " + e, Command.class);
                            }
                        }
                        break;
                }
            }
        } catch (Exception e) {
            Log.logWarning("Could not execute command: " + xml.getName() + ": " + e, Command.class);
        }

    }

}

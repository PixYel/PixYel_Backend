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
        Log.logDebug("Command from " + client.getName() + " received: \n" + xml.toStringGraph(), Command.class);
        try {
            if (!(xml.getName().equals("request"))) {
                Log.logWarning("Command from " + client.getName() + " does not start with \"request\": " + xml.getName(), Command.class);
            }
            if (xml.getName().equals("request")) {
                switch ((xml = xml.getFirstChild()).getName()) {//Cuts off the "request"

                    case "getItemList":
                        client.sendToClient(setItemList(xml));
                        break;
                    case "getItem":
                        client.sendToClient(setItem(xml));
                        break;
                    case "getItemStats":
                        int id1 = Integer.valueOf(xml.getFirstChild().getContent());

                        break;
                    case "login":
                        try {
                            client.setUserdata(User.getUser(xml.getFirstChild("storeId").getContent()));
                        } catch (UserNotFoundException ex) {
                            client.setUserdata(User.addNewUser(xml.getFirstChild("storeId").getContent()));
                        }
                        try {
                            client.getUserdata().setPublicKey(xml.getFirstChild("publicKey").getContent());
                            client.sendToClient(XML.createNewXML("loginSuccessful"));
                            Log.logInfo("Successfully logged " + client.getName() + " in", Command.class);
                        } catch (Exception e) {
                            client.sendToClient(XML.createNewXML("loginUnsuccessful"));
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
                        Log.logInfo("Successfully uploaded image by " + client.getName(), Command.class);
                        break;
                    case "flagComment":
                        int commentId = Integer.valueOf(xml.getFirstChild("id").getContent());
                        client.userdata.flagComment(commentId);
                        break;
                    case "flagItem":
                        int id3 = Integer.valueOf(xml.getFirstChild("id").getContent());
                        client.userdata.flagPicture(id3);
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
            }
        } catch (Exception e) {
            Log.logWarning("Could not execute command: " + xml.getName() + ": " + e, Command.class);
        }

    }

    public static XML setItemList(XML input) {
        XML location = input.getFirstChild();
        int longt = Integer.valueOf(location.getFirstChild("long").getContent());
        int lat = Integer.valueOf(location.getFirstChild("lat").getContent());

        XML toSend = XML.createNewXML("setItemList");
        for (int i = 0; i < 10; i++) {
            int id = 1;//TODO
            int upvotes = 1;//TODO
            int downvotes = 1;//TODO
            int votedByUser = 0;//TODO
            int rank = 42;//TODO
            String date = "14:43,1.2.16";//TODO

            XML item = toSend.addChild("item");
            item.addChildren("id", "upvotes", "downvotes", "votedByUser", "rank", "date");
            item.getFirstChild("id").setContent(String.valueOf(id));
            item.getFirstChild("upvotes").setContent(String.valueOf(upvotes));
            item.getFirstChild("downvotes").setContent(String.valueOf(downvotes));
            item.getFirstChild("votedByUser").setContent(String.valueOf(votedByUser));
            item.getFirstChild("rank").setContent(String.valueOf(rank));
            item.getFirstChild("date").setContent(date);
        }
        return toSend;
    }

    public static XML setItem(XML input) {
        int id = Integer.valueOf(input.getFirstChild("id").getContent());

        int upvotes = 1;//TODO
        int downvotes = 1;//TODO
        int votedByUser = 0;//TODO
        int rank = 42;//TODO
        String date = "14:43,1.2.16";//TODO
        String data = "...";//TODO

        XML toSend = XML.createNewXML("setItem");
        XML item = toSend.addChild("item");
        item.addChildren("id", "upvotes", "downvotes", "votedByUser", "rank", "date", "data");
        item.getFirstChild("id").setContent(String.valueOf(id));
        item.getFirstChild("upvotes").setContent(String.valueOf(upvotes));
        item.getFirstChild("downvotes").setContent(String.valueOf(downvotes));
        item.getFirstChild("votedByUser").setContent(String.valueOf(votedByUser));
        item.getFirstChild("rank").setContent(String.valueOf(rank));
        item.getFirstChild("date").setContent(date);
        item.getFirstChild("data").setContent(data);

        return toSend;
    }

    public static XML setItemStats(XML input) {
        int id = Integer.valueOf(input.getFirstChild("id").getContent());

        int upvotes = 1;//TODO
        int downvotes = 1;//TODO
        int votedByUser = 0;//TODO
        int rank = 42;//TODO
        String date = "14:43,1.2.16";//TODO

        XML toSend = XML.createNewXML("setItemStats");
        XML item = toSend.addChild("item");
        item.addChildren("id", "upvotes", "downvotes", "votedByUser", "rank", "date");
        item.getFirstChild("id").setContent(String.valueOf(id));
        item.getFirstChild("upvotes").setContent(String.valueOf(upvotes));
        item.getFirstChild("downvotes").setContent(String.valueOf(downvotes));
        item.getFirstChild("votedByUser").setContent(String.valueOf(votedByUser));
        item.getFirstChild("rank").setContent(String.valueOf(rank));
        item.getFirstChild("date").setContent(date);
        
    }

}

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
import pixyel_backend.database.exceptions.UserCreationException;
import pixyel_backend.database.exceptions.UserNotFoundException;
import pixyel_backend.database.objects.Comment;
import pixyel_backend.database.objects.Coordinate;
import pixyel_backend.database.objects.Picture;
import pixyel_backend.database.objects.User;
import pixyel_backend.xml.XML;

/**
 *
 * @author Josua Frank
 */
public class Command {

    /**
     * This method is being called when a command is arrived
     *
     * @param client The Client
     * @param xml
     * @param encrypted
     */
    public static void onCommandReceived(Client client, XML xml, boolean encrypted) {
        Log.logDebug("Command from " + client.getName() + " received: \n" + xml.toStringGraph(), Command.class);
        try {
            if (xml.getName().equals("request")) {
                xml = xml.getFirstChild();
                if (encrypted) {
                    switch (xml.getName()) {//Cuts off the "request"
                        case "getItemList":
                            client.sendToClient(getItemList(xml, client));
                            break;
                        case "getItem":
                            client.sendToClientUnencrypted(getItem(xml, client));
                            break;
                        case "getItemStats":
                            client.sendToClient(getItemStats(xml, client));
                            break;
                        case "login":
                            client.sendToClient(login(xml, client));
                            break;
                        case "echo":
                            client.sendToClient(echo(xml, client));
                            break;
                        case "vote":
                            client.sendToClient(vote(xml, client));
                            break;
                        case "flagComment":
                            client.sendToClient(flagComment(xml, client));
                            break;
                        case "flagItem":
                            client.sendToClient(flagItem(xml, client));
                            break;
                        case "getComments":
                            client.sendToClient(getComments(xml, client));
                            break;
                        case "addComment":
                            client.sendToClient(addComment(xml, client));
                            break;
                        case "disconnect":
                            disconnect(xml, client);
                            break;
                        default:
                            client.sendToClient(error(xml.getName() + " is not a valid Command", true));
                            Log.logError("Client " + client.getName() + " has send a wrong command: " + xml.getName(), Command.class);
                            break;
                    }
                } else {//unencrypted
                    switch (xml.getName()) {
                        case "upload":
                            client.sendToClient(upload(xml, client));
                            break;
                        default:
                            client.sendToClient(error(xml.getName() + " is not a valid Command, RTFS!!!", true));
                            Log.logError("Client " + client.getName() + " has send a wrong command: " + xml.getName(), Command.class);
                            break;
                    }
                }
            } else {
                client.sendToClient(error("The first node has to be called \"request\", RTFS!!!", true));
                Log.logWarning("Command from " + client.getName() + " does not start with \"request\": " + xml.getName(), Command.class);
            }
        } catch (Exception e) {
            Log.logWarning("Could not execute command: " + xml.getName() + ": " + e, Command.class);
            e.printStackTrace(System.err);
        }
    }

    /**
     *
     * @param input
     * @param client
     * @return
     */
    public static XML getItemList(XML input, Client client) {
        try {
            XML location = input.getFirstChild();
            Double longt = Double.valueOf(location.getFirstChild("long").getContent());
            Double lat = Double.valueOf(location.getFirstChild("lat").getContent());
            List<Picture> pictures = client.getUserdata().getPicturesByLocation(new Coordinate(longt, lat));

            XML toSend = XML.createNewXML("setItemList");
            pictures.stream().forEach((picture) -> {
                XML item = toSend.addChild("item");
                item.addChild("id").setContent(String.valueOf(picture.getId()));
                item.addChild("upvotes").setContent(String.valueOf(picture.getUpvotes()));
                item.addChild("downvotes").setContent(String.valueOf(picture.getDownvotes()));
                item.addChild("votedByUser").setContent(String.valueOf(picture.getVoteStatus()));
                item.addChild("rank").setContent(String.valueOf(picture.getRanking()));
                item.addChild("date").setContent(Utils.getDate(picture.getTimestamp()));
            });
            Log.logInfo("Successfully sending list of ItemStats to client " + client.getName(), Command.class);
            return toSend;
        } catch (Exception e) {
            Log.logWarning("Could not send Item List to: " + client.getName(), Command.class);
            return error("Could not send Item List: " + e, true);
        }
    }

    /**
     *
     * @param input
     * @param client
     * @return
     */
    public static XML getItem(XML input, Client client) {
        try {
            int id = Integer.valueOf(input.getFirstChild("id").getContent());

            Picture picture = client.getUserdata().getPicture(id);
            XML toSend = XML.createNewXML("setItem");
            toSend.addChild("id").setContent(String.valueOf(picture.getId()));
            toSend.addChild("upvotes").setContent(String.valueOf(picture.getUpvotes()));
            toSend.addChild("downvotes").setContent(String.valueOf(picture.getDownvotes()));
            toSend.addChild("votedByUser").setContent(String.valueOf(picture.getVoteStatus()));
            toSend.addChild("rank").setContent(String.valueOf(picture.getRanking()));
            toSend.addChild("date").setContent(Utils.getDate(picture.getTimestamp()));
            toSend.addChild("data").setContent(picture.getData());
            Log.logInfo("Successfully sending Item " + id + " to client " + client.getName(), Command.class);
            return toSend;
        } catch (Exception ex) {
            Log.logWarning("Could not load Item by " + client.getName(), Command.class);
            return error("Could not send Item: " + ex, true);
        }
    }

    /**
     *
     * @param input
     * @param client
     * @return
     */
    public static XML getItemStats(XML input, Client client) {
        try {
            int id = Integer.valueOf(input.getFirstChild("id").getContent());

            Picture picture = client.getUserdata().getPicture(id);
            XML toSend = XML.createNewXML("setItemStats");
            toSend.addChild("id").setContent(String.valueOf(picture.getId()));
            toSend.addChild("upvotes").setContent(String.valueOf(picture.getUpvotes()));
            toSend.addChild("downvotes").setContent(String.valueOf(picture.getDownvotes()));
            toSend.addChild("votedByUser").setContent(String.valueOf(picture.getDownvotes()));
            toSend.addChild("rank").setContent(String.valueOf(picture.getRanking()));
            toSend.addChild("date").setContent(Utils.getDate(picture.getTimestamp()));
            Log.logInfo("Successfully sending Item Stats of Item " + id + " to client " + client.getName(), Command.class);
            return toSend;
        } catch (Exception ex) {
            Log.logWarning("Could not get Item Stats for " + client.getName(), Command.class);
            return error("Could not get Item Stats: " + ex, true);
        }
    }

    /**
     *
     * @param input
     * @param client
     * @return
     */
    public static XML login(XML input, Client client) {
        try {
            client.setUserdata(User.getUser(input.getFirstChild("storeId").getContent()));
        } catch (UserNotFoundException ex) {
            try {
                client.setUserdata(User.addNewUser(input.getFirstChild("storeId").getContent()));
            } catch (UserCreationException ex1) {
                Log.logError("Could note create new User " + client.getName() + ": " + ex1, Command.class);
                return error("Could not create new User: " + ex1, true);
            }
        } catch (UserCreationException ex) {
            Log.logError("Could not get existing User " + client.getName() + ": " + ex, Command.class);
            return error("Could not get existing User: " + ex, true);
            //normally unreachable
        }
        XML toSend;
        try {
            client.getUserdata().setPublicKey(input.getFirstChild("publicKey").getContent());
            toSend = XML.createNewXML("loginSuccessful").setContent("true");
            Log.logInfo("Successfully logged " + client.getName() + " in", Command.class);
        } catch (Exception e) {
            toSend = XML.createNewXML("loginSuccessful").setContent("false");
            Log.logWarning("Could not log " + client.getName() + " in: " + e, Command.class);
        }
        return toSend;
    }

    /**
     *
     * @param input
     * @param client
     * @return
     */
    public static XML echo(XML input, Client client) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            String date = " " + dateFormat.format(new Date()) + " ";
            Log.logInfo("Successfully sending echo to client " + client.getName(), Command.class);
            return XML.createNewXML("echo").setContent(date);
        } catch (Exception e) {
            return error("Could not send echo: " + e, true);
        }
    }

    /**
     *
     * @param input
     * @param client
     * @return
     */
    public static XML vote(XML input, Client client) {
        try {
            int id = Integer.valueOf(input.getFirstChild("id").getContent());
            int upvote = Integer.valueOf(input.getFirstChild("upvote").getContent());
            switch (upvote) {
                case 1:
                    client.getUserdata().upvotePicture(id);
                    break;
                case -1:
                    client.getUserdata().downvotePicture(id);
                    break;
                default:
                    client.getUserdata().removeVoteFromPicture(id);
                    break;
            }

            XML toSend = XML.createNewXML("voteSuccessful");
            toSend.addChild("id").setContent(String.valueOf(id));
            toSend.addChild("success").setContent("true");

            Log.logInfo("Successfully voted Image " + id + " with " + upvote + " by Client " + client.getName(), Command.class);
            return toSend;
        } catch (Exception ex) {
            Log.logWarning("Could not vote for client " + client.getName() + ": " + ex, Command.class);
            XML toSend = XML.createNewXML("voteSuccessful");
            try {
                toSend.addChild("id").setContent(input.getFirstChild("id").getContent());
            } catch (Exception e) {
                return error("Could not get the id of the image: " + e, true);
            }
            toSend.addChild("success").setContent("true");
            return toSend;
        }
    }

    /**
     *
     * @param input
     * @param client
     * @return
     */
    public static XML upload(XML input, Client client) {
        int id = 0;
        try {
            String data = input.getFirstChild("data").getContent();
            int longt = Integer.valueOf(input.getFirstChild("long").getContent());
            int lat = Integer.valueOf(input.getFirstChild("lat").getContent());
            id = client.getUserdata().uploadPicture(data, (double) longt, (double) lat).getId();

            XML toSend = XML.createNewXML("uploadSuccessful");
            toSend.addChild("id").setContent(String.valueOf(id));
            toSend.addChild("success").setContent("true");

            Log.logInfo("Successfully uploaded image " + id + " by " + client.getName(), Command.class);
            return toSend;
        } catch (Exception e) {
            Log.logWarning("Could not upload picture by " + client.getName() + ": " + e, Command.class);
            XML toSend = XML.createNewXML("uploadSuccessful");
            if (id != 0) {
                toSend.addChild("id").setContent(String.valueOf(id));
            } else {
                return error("Could not get the id of the newly uploaded picture", true);
            }
            toSend.addChild("success").setContent("false");
            return toSend;
        }
    }

    /**
     *
     * @param input
     * @param client
     * @return
     */
    public static XML flagComment(XML input, Client client) {
        try {
            int commentId = Integer.valueOf(input.getFirstChild("id").getContent());
            client.getUserdata().flagComment(commentId);

            XML toSend = XML.createNewXML("flagCommentSuccessful");
            toSend.addChild("id").setContent(String.valueOf(commentId));
            toSend.addChild("success").setContent("true");

            Log.logInfo("Successfully flagged Comment " + commentId + " by " + client.getName(), Command.class);
            return toSend;
        } catch (Exception e) {
            Log.logWarning("Could not flag Comment by " + client.getName() + ": " + e, Command.class);
            XML toSend = XML.createNewXML("flagCommentSuccessful");
            try {
                toSend.addChild("id").setContent(input.getFirstChild("id").getContent());
            } catch (Exception ex) {
                return error("Could not get the id of the comment: " + ex, true);
            }
            toSend.addChild("success").setContent("false");
            return toSend;
        }
    }

    /**
     *
     * @param input
     * @param client
     * @return
     */
    public static XML flagItem(XML input, Client client) {
        try {
            int id = Integer.valueOf(input.getFirstChild("id").getContent());
            client.getUserdata().flagPicture(id);

            XML toSend = XML.createNewXML("flagItemSuccessful");
            toSend.addChild("id").setContent(String.valueOf(id));
            toSend.addChild("success").setContent("true");

            Log.logInfo("Successfully flagged Item " + id + " by " + client.getName(), Command.class);
            return toSend;
        } catch (Exception ex) {
            Log.logWarning("Could not flag Item by " + client.getName() + ": " + ex, Command.class);
            XML toSend = XML.createNewXML("flagItemSuccessful");
            try {
                toSend.addChild("id").setContent(input.getFirstChild("id").getContent());
            } catch (Exception e) {
                return error("Could not get the id of the picture: " + e, true);
            }
            toSend.addChild("success").setContent("false");
            return toSend;
        }
    }

    /**
     *
     * @param input
     * @param client
     * @return
     */
    public static XML getComments(XML input, Client client) {
        try {
            int id = Integer.valueOf(input.getFirstChild("id").getContent());
            List<Comment> comments = Comment.getCommentsForPicutre(id);
            XML toSend = XML.createNewXML("setComments");
            XML commentXML;
            for (Comment comment : comments) {
                commentXML = toSend.addChild("comment");
                commentXML.addChild("id").setContent(String.valueOf(comment.getCommentId()));
                commentXML.addChild("date").setContent(Utils.getDate(comment.getCommentDate()));
                commentXML.addChild("content").setContent(comment.getComment());

            }
            Log.logInfo("Successfully sending comments to client " + client.getName(), Command.class);
            return toSend;
        } catch (Exception e) {
            Log.logWarning("Could not send Comment List: " + e, Command.class);
            return error("Could not send Comment List: " + e, true);
        }
    }

    /**
     *
     * @param input
     * @param client
     * @return
     */
    public static XML addComment(XML input, Client client) {
        try {
            int id = Integer.valueOf(input.getFirstChild("id").getContent());
            String content = input.getFirstChild("content").getContent();
            client.getUserdata().addNewComment(content, id);

            XML toSend = XML.createNewXML("addCommentSuccessful");
            toSend.addChild("id").setContent(String.valueOf(id));
            toSend.addChild("success").setContent("true");

            Log.logInfo("Successfully added Comment by client " + client.getName(), Command.class);
            return toSend;
        } catch (Exception e) {
            Log.logWarning("Could not add Comment from client " + client.getName() + ": " + e, Command.class);
            XML toSend = XML.createNewXML("addCommentSuccessful");
            try {
                toSend.addChild("id").setContent(input.getFirstChild("id").getContent());
            } catch (Exception ex) {
                return error("Could not get the id of the picture: " + ex, true);
            }
            toSend.addChild("success").setContent("false");
            return toSend;
        }
    }

    public static XML error(String errorMessage, boolean fatal) {
        XML toSend = XML.createNewXML("error");
        toSend.addChild("errorMessage").setContent(errorMessage);
        toSend.addChild("isErrorFatal").setContent(String.valueOf(fatal));
        return toSend;
    }

    /**
     *
     * @param input
     * @param client
     */
    public static void disconnect(XML input, Client client) {
        client.disconnect(true);
    }

}

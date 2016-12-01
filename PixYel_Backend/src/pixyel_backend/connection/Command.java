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
import pixyel_backend.database.exceptions.CommentCreationException;
import pixyel_backend.database.exceptions.FlagFailedExcpetion;
import pixyel_backend.database.exceptions.PictureLoadException;
import pixyel_backend.database.exceptions.PictureUploadExcpetion;
import pixyel_backend.database.exceptions.UserCreationException;
import pixyel_backend.database.exceptions.UserNotFoundException;
import pixyel_backend.database.exceptions.VoteFailedException;
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
     */
    public static void onCommandReceived(Client client, XML xml, boolean encrypted) {
        Log.logDebug("Command from " + client.getName() + " received: \n" + xml.toStringGraph(), Command.class);
        if (encrypted) {

            try {
                if (!(xml.getName().equals("request"))) {
                    client.sendToClient(error("The first node has to be called \"request\", RTFS!!!", true));
                    Log.logWarning("Command from " + client.getName() + " does not start with \"request\": " + xml.getName(), Command.class);
                }
                if (xml.getName().equals("request")) {
                    switch ((xml = xml.getFirstChild()).getName()) {//Cuts off the "request"

                        case "getItemList":
                            client.sendToClient(getItemList(xml, client));
                            break;
                        case "getItem":
                            client.sendToClient(getItem(xml, client));
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
//                        case "upload":
//                            client.sendToClient(upload(xml, client));
//                            break;
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
                            client.sendToClient(error(xml.getName() + " is not a valid Command, RTFS!!!", true));
                            Log.logError("Client " + client.getName() + " has send a wrong command: " + xml.getName(), Command.class);
                            break;
                    }
                }
            } catch (Exception e) {
                Log.logWarning("Could not execute encrypted command: " + xml.getName() + ": " + e, Command.class);
                e.printStackTrace(System.err);
            }
        } else {
            try {
                if (!(xml.getName().equals("request"))) {
                    client.sendToClient(error("The first node has to be called \"request\", RTFS!!!", true));
                    Log.logWarning("Command from " + client.getName() + " does not start with \"request\": " + xml.getName(), Command.class);
                }
                if (xml.getName().equals("request")) {
                    switch ((xml = xml.getFirstChild()).getName()) {//Cuts off the "request"
                        case "upload":
                            client.sendToClientUnencrypted(upload(xml, client));
                            break;
                        default:
                            client.sendToClient(error(xml.getName() + " is not a valid Command, RTFS!!!", true));
                            Log.logError("Client " + client.getName() + " has send a wrong command: " + xml.getName(), Command.class);
                            break;
                    }
                }
            } catch (Exception e) {
                Log.logWarning("Could not execute unencrypted command: " + xml.getName() + ": " + e, Command.class);
                e.printStackTrace(System.err);
            }

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
            int longt = Integer.valueOf(location.getFirstChild("long").getContent());
            int lat = Integer.valueOf(location.getFirstChild("lat").getContent());

            List<Picture> pictures = client.getUserdata().getPicturesByLocation(new Coordinate(longt, lat));

            XML toSend = XML.createNewXML("setItemList");
            pictures.stream().forEach((picture) -> {
                XML item = toSend.addChild("item");
                item.addChildren("id", "upvotes", "downvotes", "votedByUser", "rank", "date");
                item.getFirstChild("id").setContent(String.valueOf(picture.getId()));
                item.getFirstChild("upvotes").setContent(String.valueOf(picture.getUpvotes()));
                item.getFirstChild("downvotes").setContent(String.valueOf(picture.getDownvotes()));
                item.getFirstChild("votedByUser").setContent(String.valueOf(picture.getVoteStatus()));
                item.getFirstChild("rank").setContent(String.valueOf(picture.getRanking()));
                item.getFirstChild("date").setContent(Utils.getDate(picture.getTimestamp()));
            });
            Log.logInfo("Sending list of ItemStats to client " + client.getName(), Command.class);
            return toSend;

        } catch (Exception e) {
            Log.logWarning("Could not send Item List to: " + client.getName(), Command.class);
            return error("Something went wrong during the item sending process: " + e, true);
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
            XML item = toSend.addChild("item");
            item.addChildren("id", "upvotes", "downvotes", "votedByUser", "rank", "date", "data");
            item.getFirstChild("id").setContent(String.valueOf(picture.getId()));
            item.getFirstChild("upvotes").setContent(String.valueOf(picture.getUpvotes()));
            item.getFirstChild("downvotes").setContent(String.valueOf(picture.getDownvotes()));
            item.getFirstChild("votedByUser").setContent(String.valueOf(picture.getVoteStatus()));
            item.getFirstChild("rank").setContent(String.valueOf(picture.getRanking()));
            item.getFirstChild("date").setContent(Utils.getDate(picture.getTimestamp()));
            item.getFirstChild("data").setContent(picture.getData());
            Log
                    .logInfo("Sending Item " + id + " to client " + client.getName(), Command.class
                    );
            return toSend;

        } catch (Exception ex) {
            Log.logWarning("Could not load Picture by " + client.getName(), Command.class
            );
            return error("Could not send Image: " + ex, false);
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
            XML item = toSend.addChild("item");
            item.addChildren("id", "upvotes", "downvotes", "votedByUser", "rank", "date");
            item.getFirstChild("id").setContent(String.valueOf(picture.getId()));
            item.getFirstChild("upvotes").setContent(String.valueOf(picture.getUpvotes()));
            item.getFirstChild("downvotes").setContent(String.valueOf(picture.getDownvotes()));
            item.getFirstChild("votedByUser").setContent(String.valueOf(picture.getDownvotes()));
            item.getFirstChild("rank").setContent(String.valueOf(picture.getRanking()));
            item.getFirstChild("date").setContent(Utils.getDate(picture.getTimestamp()));
            Log
                    .logInfo("Sending ItemStats of Item " + id + " to client " + client.getName(), Command.class
                    );
            return toSend;

        } catch (PictureLoadException ex) {
            Log.logWarning("Could not get Pictures for " + client.getName(), Command.class
            );
            return error("Could not fetch Item Stats: " + ex, false);
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
                Log.logError("Could note create new User " + client.getName() + ": " + ex1, Command.class
                );
                return error("Could not create new User: " + ex1, true);

            }
        } catch (UserCreationException ex) {
            Log.logError("Could not get existing User " + client.getName() + ": " + ex, Command.class
            );
            return null;
        }
        XML toSend;
        try {
            client.getUserdata().setPublicKey(input.getFirstChild("publicKey").getContent());
            toSend = XML.createNewXML("loginSuccessful").setContent("true");
            Log
                    .logInfo("Successfully logged " + client.getName() + " in", Command.class
                    );
        } catch (Exception e) {
            toSend = XML.createNewXML("loginUnsuccessful").setContent("false");
            Log
                    .logWarning("Failed to log " + client.getName() + " in: " + e, Command.class
                    );
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
            Log
                    .logInfo("Sending echo to client " + client.getName(), Command.class
                    );
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
        int id = Integer.valueOf(input.getFirstChild("id").getContent());
        int upvote = Integer.valueOf(input.getFirstChild("upvote").getContent());

        boolean voteSuccessful = true;
        try {
            if (upvote == 1) {
                client.getUserdata().upvotePicture(id);
            } else if (upvote == -1) {
                client.getUserdata().downvotePicture(id);
            } else {
                client.getUserdata().removeVoteFromPicture(id);

            }
        } catch (VoteFailedException ex) {
            Log.logWarning("Vote by client " + client.getName() + " failed: " + ex, Command.class
            );
            voteSuccessful = false;
        }

        XML toSend = XML.createNewXML("voteSuccessful");
        toSend.addChild("id").setContent(String.valueOf(id));
        toSend.addChild("success").setContent(String.valueOf(voteSuccessful));

        if (voteSuccessful) {
            Log.logInfo("Client " + client.getName() + " has voted the image " + id + " with upvote=" + upvote, Command.class
            );

        } else {
            Log.logInfo("Client " + client.getName() + " has NOT SUCCESSFULLY voted the image " + id + " with upvote=" + upvote, Command.class
            );
        }
        return toSend;
    }

    /**
     *
     * @param input
     * @param client
     * @return
     */
    public static XML upload(XML input, Client client) {
        String data = input.getFirstChild("data").getContent();
        int longt = Integer.valueOf(input.getFirstChild("long").getContent());
        int lat = Integer.valueOf(input.getFirstChild("lat").getContent());

        int id = 0;
        boolean uploadSuccessful = true;
        try {
            id = client.getUserdata().uploadPicture(data, (double) longt, (double) lat).getId();
        } catch (PictureUploadExcpetion ex) {
            uploadSuccessful = false;
            Log
                    .logWarning("Uploading picture by " + client.getName() + " failed: " + ex, Command.class
                    );
        }

        XML toSend = XML.createNewXML("uploadSuccessful");
        toSend.addChild("id").setContent(String.valueOf(id));
        toSend.addChild("success").setContent(String.valueOf(uploadSuccessful));

        if (uploadSuccessful) {
            Log.logInfo("Successfully uploaded image " + id + " by " + client.getName(), Command.class
            );

        } else {
            Log.logWarning("UNSuccessfully uploaded image " + id + " by " + client.getName(), Command.class
            );
        }
        return toSend;
    }

    /**
     *
     * @param input
     * @param client
     * @return
     */
    public static XML flagComment(XML input, Client client) {
        int commentId = Integer.valueOf(input.getFirstChild("id").getContent());

        boolean flagCommentSuccessful = true;
        try {
            client.getUserdata().flagComment(commentId);

        } catch (FlagFailedExcpetion ex) {
            Log.logWarning("Failed to flag Comment by " + client.getName() + ": " + ex, Command.class
            );
            flagCommentSuccessful = false;
        }

        XML toSend = XML.createNewXML("flagCommentSuccessful");
        toSend.addChild("id").setContent(String.valueOf(commentId));
        toSend.addChild("success").addChild(String.valueOf(flagCommentSuccessful));

        if (flagCommentSuccessful) {
            Log.logInfo("Successfully flagged Comment " + commentId + " by " + client.getName(), Command.class
            );

        } else {
            Log.logWarning("UNSuccessfully flagged Comment " + commentId + " by " + client.getName(), Command.class
            );
        }
        return toSend;
    }

    /**
     *
     * @param input
     * @param client
     * @return
     */
    public static XML flagItem(XML input, Client client) {
        int id = Integer.valueOf(input.getFirstChild("id").getContent());

        boolean flagItemSuccessful = true;//TODO
        try {
            client.getUserdata().flagPicture(id);

        } catch (FlagFailedExcpetion ex) {
            Log.logWarning("Failed to flag Item by " + client.getName() + ": " + ex, Command.class
            );
            flagItemSuccessful = false;
        }

        XML toSend = XML.createNewXML("flagItemSuccessful");
        toSend.addChild("id").setContent(String.valueOf(id));
        toSend.addChild("success").addChild(String.valueOf(flagItemSuccessful));

        if (flagItemSuccessful) {
            Log.logInfo("Successfully flagged Item " + id + " by " + client.getName(), Command.class
            );

        } else {
            Log.logWarning("UNSuccessfully flagged Item " + id + " by " + client.getName(), Command.class
            );
        }
        return toSend;
    }

    /**
     *
     * @param input
     * @param client
     * @return
     */
    public static XML getComments(XML input, Client client) {
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
        Log.logInfo("Sending comments to client " + client.getName(), Command.class
        );
        return toSend;
    }

    /**
     *
     * @param input
     * @param client
     * @return
     */
    public static XML addComment(XML input, Client client) {
        int id = Integer.valueOf(input.getFirstChild("id").getContent());
        String content = input.getFirstChild("content").getContent();
        boolean successfulAddedComment = true;

        try {
            client.getUserdata().addNewComment(content, id);

        } catch (CommentCreationException ex) {
            Log.logWarning("Could not add Comment from client " + client.getName(), Command.class
            );
            successfulAddedComment = false;
        }

        XML toSend = XML.createNewXML("addCommentSuccessful");
        toSend.addChild("id").setContent(String.valueOf(id));
        toSend.addChild("success").setContent(String.valueOf(successfulAddedComment));

        if (successfulAddedComment) {
            Log.logInfo("Successfully added Comment by client " + client.getName(), Command.class
            );

        } else {
            Log.logWarning("UNSuccessfully added Comment by client " + client.getName(), Command.class
            );
        }
        return toSend;
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

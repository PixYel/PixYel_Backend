/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend.connection;

import pixyel_backend.connection.socket.SocketClient;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import pixyel_backend.Log;
import pixyel_backend.connection.encryption.Encryption;
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
     * @param client The SocketClient
     * @param xml
     * @param encrypted
     */
    public static String onCommandReceived(Client client, XML xml, boolean encrypted) {
        Log.logDebug("Command from " + client.getName() + " received: \n" + xml.toStringGraph(), Command.class);
        try {
            if (xml.getName().equals("request")) {
                xml = xml.getFirstChild();
                if (encrypted) {
                    switch (xml.getName()) {//Cuts off the "request"
                        case "getItemList":
                            return xmlToEncryptedString(getItemList(xml, client), client);
                        case "getItemListByDate":
                            return xmlToEncryptedString(getItemListByDate(xml, client), client);
                        case "getItemListLikedByMe":
                            return xmlToEncryptedString(getItemListLikedByMe(xml, client), client);
                        case "getItem":
                            return xmlToUnencryptedString(getItem(xml, client));
                        case "getItemStats":
                            return xmlToEncryptedString(getItemStats(xml, client), client);
                        case "login":
                            return xmlToEncryptedString(login(xml, client), client);
                        case "echo":
                            return xmlToEncryptedString(echo(xml, client), client);
                        case "vote":
                            return xmlToEncryptedString(vote(xml, client), client);
                        case "flagComment":
                            return xmlToEncryptedString(flagComment(xml, client), client);
                        case "flagItem":
                            return xmlToEncryptedString(flagItem(xml, client), client);
                        case "getComments":
                            return xmlToEncryptedString(getComments(xml, client), client);
                        case "addComment":
                            return xmlToEncryptedString(addComment(xml, client), client);
                        case "disconnect":
                            disconnect(xml, client);
                            return "";
                        default:
                            Log.logError("Client " + client.getName() + " has send a wrong command: " + xml.getName(), Command.class);
                            return xmlToEncryptedString(error(xml.getName() + " is not a valid Command", true), client);
                    }
                } else {//unencrypted
                    switch (xml.getName()) {
                        case "upload":
                            return xmlToEncryptedString(upload(xml, client), client);
                        default:
                            Log.logError("Client " + client.getName() + " has send a wrong command: " + xml.getName(), Command.class);
                            return xmlToEncryptedString(error(xml.getName() + " is not a valid Command", true), client);
                    }
                }
            } else {
                Log.logWarning("Command from " + client.getName() + " does not start with \"request\": " + xml.getName(), Command.class);
                return xmlToEncryptedString(error("The first node has to be called \"request\"", true), client);
            }
        } catch (Exception e) {
            Log.logWarning("Could not execute command: " + xml.getName() + ": " + e, Command.class);
            e.printStackTrace(System.err);
            return xmlToEncryptedString(error("Could not execute command: " + e, true), client);
        }
    }

    private static String xmlToEncryptedString(XML xml, Client client) {
        if (!xml.getName().equals("reply")) {
            xml = XML.createNewXML("reply").addChild(xml);
        }
        Log.logDebug("PLAIN_TO_SEND: " + xml, Command.class);

        if (client.getUserdata() != null && client.getUserdata().getPublicKey() != null) {
            try {
                return Encryption.encrypt(xml.toString(), client.getUserdata().getPublicKey());
            } catch (Encryption.EncryptionException ex) {
                Log.logError("Could not encrypt message for " + client.getName(), Command.class);
                return error("Could not encrypt message: " + ex, true).toString();
            }
        } else {
            Log.logWarning("Client " + client + " needs to be logged in first!", Command.class);
            return error("You need to log in first!", true).toString();
        }
    }

    private static String xmlToUnencryptedString(XML xml) {
        if (!xml.getName().equals("reply")) {
            xml = XML.createNewXML("reply").addChild(xml);
        }
        String result = xml.toString();
        Log.logDebug("PLAIN_TO_SEND: " + result, Command.class);
        return result;
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
                item.addChild("date").setContent(Utils.getDate(picture.getUploadDate(), picture.getUploadTime()));
            });
            Log.logInfo("Successfully sending list of ItemStats by rank and coordinate to client " + client.getName(), Command.class);
            return toSend;
        } catch (Exception e) {
            Log.logWarning("Could not send Item List to: " + client.getName(), Command.class);
            return error("Could not send Item List: " + e, true);
        }
    }

    public static XML getItemListByDate(XML input, Client client) {
        List<Picture> pictures = client.getUserdata().newestPictures(10);

        XML toSend = XML.createNewXML("setItemList");
        pictures.stream().forEach((picture) -> {
            XML item = toSend.addChild("item");
            item.addChild("id").setContent(String.valueOf(picture.getId()));
            item.addChild("upvotes").setContent(String.valueOf(picture.getUpvotes()));
            item.addChild("downvotes").setContent(String.valueOf(picture.getDownvotes()));
            item.addChild("votedByUser").setContent(String.valueOf(picture.getVoteStatus()));
            item.addChild("rank").setContent(String.valueOf(picture.getRanking()));
            item.addChild("date").setContent(Utils.getDate(picture.getUploadDate(), picture.getUploadTime()));
        });
        Log.logInfo("Successfully sending list of ItemStats sorted by date to client " + client.getName(), Command.class);
        return toSend;
    }

    public static XML getItemListLikedByMe(XML input, Client client) {
        List<Picture> pictures = client.getUserdata().getAllLikedPictures();

        XML toSend = XML.createNewXML("setItemList");
        pictures.stream().forEach((picture) -> {
            XML item = toSend.addChild("item");
            item.addChild("id").setContent(String.valueOf(picture.getId()));
            item.addChild("upvotes").setContent(String.valueOf(picture.getUpvotes()));
            item.addChild("downvotes").setContent(String.valueOf(picture.getDownvotes()));
            item.addChild("votedByUser").setContent(String.valueOf(picture.getVoteStatus()));
            item.addChild("rank").setContent(String.valueOf(picture.getRanking()));
            item.addChild("date").setContent(Utils.getDate(picture.getUploadDate(), picture.getUploadTime()));
        });
        Log.logInfo("Successfully sending list of ItemStats liked by the client to client " + client.getName(), Command.class);
        return toSend;
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
            toSend.addChild("date").setContent(Utils.getDate(picture.getUploadDate(), picture.getUploadTime()));
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
            toSend.addChild("date").setContent(Utils.getDate(picture.getUploadDate(), picture.getUploadTime()));
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
        if (client instanceof SocketClient) {
            try {
                ((SocketClient) client).setUserdata(User.getUser(input.getFirstChild("storeId").getContent()));
            } catch (UserNotFoundException ex) {
                try {
                    ((SocketClient) client).setUserdata(User.addNewUser(input.getFirstChild("storeId").getContent()));
                } catch (UserCreationException ex1) {
                    Log.logError("Could note create new User " + client.getName() + ": " + ex1, Command.class);
                    return error("Could not create new User: " + ex1, true);
                }
            } catch (UserCreationException ex) {
                Log.logError("Could not get existing User " + client.getName() + ": " + ex, Command.class);
                return error("Could not get existing User: " + ex, true);
                //normally unreachable
            }
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
            Double longt = Double.valueOf(input.getFirstChild("long").getContent());
            Double lat = Double.valueOf(input.getFirstChild("lat").getContent());
            id = client.getUserdata().uploadPicture(data, new Coordinate(longt, lat));

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
        if (client instanceof SocketClient) {
            SocketClient sClient = (SocketClient) client;
            sClient.sendToClient(xmlToEncryptedString(XML.createNewXML("disconnected"), sClient));
            sClient.disconnect();
        }
    }

}

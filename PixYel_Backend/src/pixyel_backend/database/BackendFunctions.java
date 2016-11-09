/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend.database;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import pixyel_backend.database.exceptions.CommentCreationException;
import pixyel_backend.database.objects.Comment;
import pixyel_backend.xml.XML;

/**
 *
 * @author Yannick
 */
public class BackendFunctions {

    private final int userId;

    public BackendFunctions(int userid) {
        this.userId = userid;
    }

    /**
     * Return all comments for a picture as XML Structure
     * <p>
     * Example:
     * <commentsforpicture pictureId="0">
     * <comment creationdate="09.11.2016-16:15:22" commentText="lorem ipsum">
     * <comment creationdate="11.11.2016-16:15:22" commentText="lorem ipsum">
     * <comment creationdate="12.11.2016-16:15:22" commentText="lorem ipsum">
     * <comment creationdate="13.11.2016-16:15:22" commentText="lorem ipsum">
     * </commentsforpicture>
     *
     * @param pictureId
     * @return all comments of a picture as a XML sturcture
     * @throws SQLException
     * @throws CommentCreationException
     */
    public XML getCommentsForPicutre(int pictureId) throws SQLException, CommentCreationException {
        XML out = XML.createNewXML("commentsforpicture");
        out.addAttribute("pictureId", String.valueOf(pictureId));
        List<Comment> allComments = Comment.getCommentsForPicutre(pictureId);
        int iterator = 0;
        for (Comment currentComment : allComments) {
            out.addChild("comment");
            out.getChildren().get(iterator).addAttribute("creationDate", currentComment.getCommentDate().toString());
            out.getChildren().get(iterator).addAttribute("commentText", currentComment.getComment());
            iterator++;
        }
        return out;
    }

    /**
     * @see pixyel_backend.database.objects.Comment.addFlag
     */
    public void flagComment(int commentId) {
        try {
            Comment.addFlag(userId, commentId);
        } catch (SQLException ex) {
            Logger.getLogger(BackendFunctions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @see pixyel_backend.database.objects.Comment
     */
    public void addNewComment(String text, int refersToPicuture) {
        Comment.newComment(refersToPicuture, userId, text);
    }

    public XML getPicturesList(int xCordinate, int yCordinate) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public XML getPicturesData(String listAsString) {
        List<String> allRequestedPictures = Arrays.asList(listAsString);
        for (String currentPictureIdAsString : allRequestedPictures) {
            ////Picutres holen zu XML hinzufügen 
        }
        return null;
    }

    public void flagPicture(int pictureId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void uploadPicture(String picturedata) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend.database.objects;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import pixyel_backend.Log;
import pixyel_backend.database.DbConnection;
import pixyel_backend.database.exceptions.CommentCreationException;
import pixyel_backend.database.exceptions.CommentNotFoundException;
import pixyel_backend.database.exceptions.DbConnectionException;

/**
 *
 * @author Groove
 */
public class Comment {

    private final int commentId;
    private int pictureId;
    private int userId;
    private String comment;
    private final Date commentDate;
    private int flags;
    private List<String> flaggedBy;
    private DbConnection con;

    public Comment(int commentId) throws CommentCreationException {

        try {
            this.con = new DbConnection();
            PreparedStatement statement = con.getPreparedStatement("SELECT * FROM comments WHERE commentid LIKE ?");
            statement.setInt(1, commentId);
            ResultSet result = statement.executeQuery();
            if (result == null || !result.isBeforeFirst()) {
                throw new CommentNotFoundException();
            }
            result.next();
            this.commentId = commentId;
            this.pictureId = result.getInt("pictureid");
            this.userId = result.getInt("userid");
            this.comment = result.getString("comment");
            this.commentDate = result.getDate("comment_date");
            String flaggedByAsString = result.getString("flags");
            this.flaggedBy = Arrays.asList(flaggedByAsString);
            this.flags = this.flaggedBy.size();

            
        } catch (SQLException | DbConnectionException ex) {
            Log.logError("Could not read Commentinformation from database - rootcause: " + ex.getMessage(), this);
            throw new CommentCreationException();
        }
    }

    /**
     * @return the commentId
     */
    public int getCommentId() {
        return commentId;
    }

    /**
     * @return the pictureId
     */
    public int getPictureId() {
        return pictureId;
    }

    /**
     * @param pictureId the pictureId to set
     */
    public void setPictureId(int pictureId) {
        this.pictureId = pictureId;
    }

    /**
     * @return the userId
     */
    public int getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * @param comment the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * @return the commentDate
     */
    public Date getCommentDate() {
        return commentDate;
    }

    /**
     * @return the flags
     */
    public int getFlags() {
        return flags;
    }

    public void addFlag() {
        this.flags++;
        updateCommentValue("flags", this.flags);
    }

    public boolean isFlaggedBy(int userid) {
        return (this.flaggedBy.contains(String.valueOf(userid)));
    }

    /**
     *
     * @param column
     * @param toValue
     */
    private void updateCommentValue(String column, int toValue) {
        try {
            PreparedStatement sta = con.getPreparedStatement("UPDATE users SET " + column + " = ? WHERE id LIKE " + this.commentId);
            sta.setInt(1, toValue);
            sta.execute();
            sta.close();
        } catch (SQLException ex) {
            Log.logError("couldnt update user value \"" + column + "\" - rootcause:" + ex.getMessage(), this);
        }
    }
}

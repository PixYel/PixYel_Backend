/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend.database.objects;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import pixyel_backend.Log;
import pixyel_backend.database.DbConnection;
import pixyel_backend.database.SqlUtils;
import pixyel_backend.database.exceptions.CommentCreationException;
import pixyel_backend.database.exceptions.CommentNotFoundException;
import pixyel_backend.database.exceptions.NotImplementedException;

/**
 *
 * @author Groove
 */
public class Comment {

    private final int commentId;
    private final int pictureId;
    private final int userId;
    private final String comment;
    private final Date commentDate;
    private int flags;
    private List<String> flaggedBy;     //Contains userIds that flagged the comment
    private DbConnection con;

    public Comment(int commentId, DbConnection con) throws CommentCreationException {

        try {
            this.con = con;
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
            if (result.getString("flags") != null) {
                String flaggedByAsString = result.getString("flags");
                this.flaggedBy = Arrays.asList(flaggedByAsString);
                this.flags = this.flaggedBy.size();
            } else {
                this.flaggedBy = new ArrayList();
                this.flags = 0;
            }
        } catch (SQLException ex) {
            Log.logError("Could not read Commentinformation from database - rootcause: " + ex.getMessage(), Comment.class);
            throw new CommentCreationException();
        }
    }

    public static void newComment(int pictureId, int userId, String comment, DbConnection con) {
        PreparedStatement statement;
        try {
            statement = con.getPreparedStatement("INSERT INTO comments (pictureid, userid, comment) VALUES(?,?,?)");

            if (comment != null && comment.length() >= 2) {
                comment = SqlUtils.escapeString(comment);
                statement.setInt(1, pictureId);
                statement.setInt(2, userId);
                statement.setString(3, comment);
                statement.executeUpdate();
            } else {
                Log.logInfo("Failed to create comment for user \"" + userId + "\" - rootcause: Comment is NULL or to short", Comment.class);
            }
        } catch (SQLException ex) {
            Log.logError("Could not read Commentinformation from database - rootcause: " + ex.getMessage(), Comment.class);
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
     * @return the userId
     */
    public int getUserId() {
        return userId;
    }

    /**
     * @return the comment
     */
    public String getComment() {
        return comment;
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

    public void addFlag(int userid) {
        this.flags++;
        this.flaggedBy.add(Integer.toString(userid));
        StringBuffer flaggedByAsString = new StringBuffer();
        for(String currentString:this.flaggedBy){
            flaggedByAsString.append(currentString);
        }
        updateCommentValue("flags", flaggedByAsString.toString());
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
    
    private void updateCommentValue(String column, String toValue) {
        try {
            PreparedStatement sta = con.getPreparedStatement("UPDATE users SET " + column + " = ? WHERE id LIKE " + this.commentId);
            sta.setString(1, toValue);
            sta.execute();
            sta.close();
        } catch (SQLException ex) {
            Log.logError("couldnt update user value \"" + column + "\" - rootcause:" + ex.getMessage(), this);
        }
    }
    
    public  static List getCommentsForPicutre(int pictureId, DbConnection con){
        throw new NotImplementedException();
    }
    
    public void deleteComment(){
        throw new NotImplementedException();
    }
}

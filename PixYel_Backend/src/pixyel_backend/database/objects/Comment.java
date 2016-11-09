package pixyel_backend.database.objects;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import pixyel_backend.Log;
import pixyel_backend.database.MysqlConnector;
import pixyel_backend.database.SqlUtils;
import pixyel_backend.database.exceptions.CommentCreationException;
import pixyel_backend.database.exceptions.CommentNotFoundException;

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
    private Connection con;

    public Comment(int commentId) throws CommentCreationException {
        try {
            this.con = MysqlConnector.getConnection();
            PreparedStatement statement = con.prepareStatement("SELECT * FROM comments WHERE commentid LIKE ?");
            statement.setInt(1, commentId);
            ResultSet result = statement.executeQuery();
            statement.close();
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

    /**
     * IMPORTANT: ResultSet Pointer must already point at an entry, won't go to
     * next result by itself
     *
     * @param result
     * @throws SQLException
     */
    public Comment(ResultSet result) throws SQLException {
        this.commentId = result.getInt("commentid");
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

    }

    public static void newComment(int pictureId, int userId, String comment) {
        Connection con = MysqlConnector.getConnection();
        PreparedStatement statement;
        try {
            statement = con.prepareStatement("INSERT INTO comments (pictureid, userid, comment) VALUES(?,?,?)");

            if (comment != null && comment.length() >= 2) {
                comment = SqlUtils.escapeString(comment);
                statement.setInt(1, pictureId);
                statement.setInt(2, userId);
                statement.setString(3, comment);
                statement.executeUpdate();
                statement.close();
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

    public static synchronized void addFlag(int userid, int commentid) throws SQLException {
        Connection con = MysqlConnector.getConnection();
        PreparedStatement sta = con.prepareStatement("SELECT flags FROM comments WHERE commentid LIKE ?");
        sta.setInt(1, commentid);
        ResultSet result = sta.executeQuery();
        sta.close();
        result.next();

        List<String> staticFlaggedBy;
        //flaggedBy String to List
        String flaggedByAsString;
        if (result.getString("flags") != null) {
            flaggedByAsString = result.getString("flags");
            staticFlaggedBy = Arrays.asList(flaggedByAsString);
        } else {
            staticFlaggedBy = new ArrayList();
        }
        StringBuilder flaggedByAsStringBuilder = new StringBuilder();
        for (String currentString : staticFlaggedBy) {
            flaggedByAsStringBuilder.append(currentString);
        }
        flaggedByAsString = SqlUtils.escapeString(flaggedByAsStringBuilder.toString());
        updateCommentValue("flags", flaggedByAsString, commentid);
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
            PreparedStatement sta = con.prepareStatement("UPDATE users SET " + column + " = ? WHERE id LIKE " + this.commentId);
            sta.setInt(1, toValue);
            sta.execute();
            sta.close();
        } catch (SQLException ex) {
            Log.logError("couldnt update user value \"" + column + "\" - rootcause:" + ex.getMessage(), this);
        }
    }

    private void updateCommentValue(String column, String toValue) {
        try {
            PreparedStatement sta = con.prepareStatement("UPDATE users SET " + column + " = ? WHERE id LIKE " + this.commentId);
            sta.setString(1, toValue);
            sta.execute();
            sta.close();
        } catch (SQLException ex) {
            Log.logError("couldnt update user value \"" + column + "\" - rootcause:" + ex.getMessage(), this);
        }
    }
    
    private static void updateCommentValue(String column, String toValue, int commentId) {
        try {
            Connection con = MysqlConnector.getConnection();
            PreparedStatement sta = con.prepareStatement("UPDATE users SET " + column + " = ? WHERE id LIKE " + commentId);
            sta.setString(1, toValue);
            sta.execute();
            sta.close();
        } catch (SQLException ex) {
            Log.logError("couldnt update user value \"" + column + "\" - rootcause:" + ex.getMessage(), commentId);
        }
    }

    /**
     *
     * @param pictureId
     * @param con
     * @return commentList -> all comments that match the PictureId in order to
     * their insertion date.
     * @throws SQLException
     * @throws CommentCreationException
     */
    public static List<Comment> getCommentsForPicutre(int pictureId) throws SQLException, CommentCreationException {
        Connection con = MysqlConnector.getConnection();
        PreparedStatement sta = con.prepareStatement("SELECT * FROM comment WHERE pictureid LIKE ? ORDER BY comment_date ASC");
        sta.setInt(1, pictureId);
        ResultSet resultSet = sta.executeQuery();
        List<Comment> commentList = new LinkedList();
        while (resultSet.next()) {
            Comment comment = new Comment(resultSet);
            commentList.add(comment);
        }
        return commentList;
    }

    public static void deleteComment(int commentId) throws SQLException {
        Connection con = MysqlConnector.getConnection();
        PreparedStatement sta = con.prepareStatement("DELETE FROM comments WHERE commentid LIKE ?");
        sta.setInt(1, commentId);
        sta.executeQuery();
    }
}
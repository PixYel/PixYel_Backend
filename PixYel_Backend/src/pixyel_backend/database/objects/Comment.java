package pixyel_backend.database.objects;

import pixyel_backend.database.exceptions.FlagFailedExcpetion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    public Comment(int commentId) throws CommentCreationException {
        try {
            ResultSet result;
            try (PreparedStatement statement = MysqlConnector.getConnection().prepareStatement("SELECT * FROM comments WHERE commentid = ?")) {
                statement.setInt(1, commentId);
                result = statement.executeQuery();
            }
            if (result == null || !result.isBeforeFirst()) {
                throw new CommentNotFoundException();
            }
            result.next();
            this.commentId = commentId;
            this.pictureId = result.getInt("pictureid");
            this.userId = result.getInt("userid");
            this.comment = result.getString("comment");
            this.commentDate = result.getDate("comment_date");
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
     * Adds a flag
     *
     * @param userId
     * @param commentId
     * @throws FlagFailedExcpetion
     */
    public static void addFlag(int userId, int commentId) throws FlagFailedExcpetion {
        try (PreparedStatement statement = MysqlConnector.getConnection().prepareStatement("SELECT id FROM commentflags WHERE commentid = ? AND  userid = ?")) {
            statement.setInt(1, commentId);
            statement.setInt(2, userId);
            ResultSet result = statement.executeQuery();
            if (result == null || !result.isBeforeFirst()) {
                try (PreparedStatement instertStatement = MysqlConnector.getConnection().prepareStatement("INSERT INTO commentflags (commentid,userid) VALUES (?,?)")) {
                    instertStatement.setInt(1, commentId);
                    instertStatement.setInt(2, userId);
                    instertStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            Log.logWarning("Failed to flag comment " + commentId + " rootcause: - " + ex, Comment.class);
            throw new FlagFailedExcpetion();

        }
    }

    /**
     *
     * @param pictureId
     * @return commentList -> all comments that match the PictureId in order to
     * their insertion date.
     */
    public static List<Comment> getCommentsForPicutre(int pictureId) {
        Connection con = MysqlConnector.getConnection();
        PreparedStatement sta;
        List<Comment> commentList = new LinkedList();
        try {
            sta = con.prepareStatement("SELECT * FROM comment WHERE pictureid LIKE ? ORDER BY comment_date ASC");

            sta.setInt(1, pictureId);
            ResultSet resultSet = sta.executeQuery();

            while (resultSet.next()) {
                Comment comment = new Comment(resultSet);
                commentList.add(comment);
            }
        } catch (SQLException ex) {
            Log.logError("Couldnt read picture Table", Comment.class);
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

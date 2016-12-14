package pixyel_backend.database.objects;

import pixyel_backend.database.exceptions.FlagFailedExcpetion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import pixyel_backend.Log;
import pixyel_backend.database.Columns;
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

    /**
     * Creates a new Comment-object by reading out all information from the
     * database based on the given id
     *
     * @param commentId
     * @throws CommentCreationException
     */
    public Comment(int commentId) throws CommentCreationException {
        try {
            ResultSet result;
            try (PreparedStatement statement = MysqlConnector.getConnection().prepareStatement("SELECT * FROM comments WHERE " + Columns.ID + " = ?")) {
                statement.setInt(1, commentId);
                result = statement.executeQuery();
            }
            if (result == null || !result.isBeforeFirst()) {
                throw new CommentNotFoundException();
            }
            result.next();
            this.commentId = commentId;
            this.pictureId = result.getInt(Columns.PICTURE_ID);
            this.userId = result.getInt(Columns.USER_ID);
            this.comment = result.getString(Columns.TEXT);
            this.commentDate = result.getDate(Columns.CREATION_DATE);
        } catch (SQLException ex) {
            Log.logError("Could not read Commentinformation from database - rootcause: " + ex.getMessage(), Comment.class);
            throw new CommentCreationException();
        }
    }

    /**
     * Returns the next comment from a ResultSet
     *
     * IMPORTANT: ResultSet Pointer must already point at an entry, won't go to
     * next result by itself
     *
     * @param result
     * @throws pixyel_backend.database.exceptions.CommentCreationException
     */
    public Comment(ResultSet result) throws CommentCreationException {
        try {
            this.commentId = result.getInt(Columns.ID);
            this.pictureId = result.getInt(Columns.PICTURE_ID);
            this.userId = result.getInt(Columns.USER_ID);
            this.comment = result.getString(Columns.TEXT);
            this.commentDate = result.getDate(Columns.CREATION_DATE);
        } catch (SQLException ex) {
            Log.logError("Could not create Comment  from resultset - rootcause: " + ex.getMessage(), Comment.class);
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
     * Insert a new comment in the Database.
     *
     * @param pictureId
     * @param userId
     * @param comment
     * @throws CommentCreationException
     */
    public static void addComment(int pictureId, int userId, String comment) throws CommentCreationException {
        try (PreparedStatement statement = MysqlConnector.getConnection().prepareStatement("INSERT INTO comments (" + Columns.PICTURE_ID + ", " + Columns.USER_ID + ", " + Columns.TEXT + ") VALUES(?,?,?)")) {
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
            throw new CommentCreationException();
        }
    }

    /**
     * Adds a flag to a comment (Database insert)
     *
     * @param userId
     * @param commentId
     * @throws FlagFailedExcpetion
     */
    public static void flagComment(int userId, int commentId) throws FlagFailedExcpetion {
        try (PreparedStatement statement = MysqlConnector.getConnection().prepareStatement("INSERT IGNORE INTO commentflags (" + Columns.COMMENT_ID + "," + Columns.USER_ID + ") VALUES (?,?)")) {
            statement.setInt(1, commentId);
            statement.setInt(2, userId);
            statement.executeUpdate();
        } catch (SQLException ex) {
            Log.logWarning("Failed to flag comment " + commentId + " rootcause: - " + ex, Comment.class);
            throw new FlagFailedExcpetion();

        }
    }

    /**
     * Returns a list of all comments that are linked to the given pictureId
     *
     * @param pictureId
     * @return commentList -> all comments that match the PictureId in order to
     * their insertion date.
     */
    public static List<Comment> getCommentsForPicutre(int pictureId) {
        List<Comment> commentList = new LinkedList();
        try (PreparedStatement sta = MysqlConnector.getConnection().prepareStatement("SELECT * FROM comments WHERE " + Columns.PICTURE_ID + " LIKE ? ORDER BY " + Columns.CREATION_DATE + " ASC")) {
            sta.setInt(1, pictureId);
            ResultSet resultSet = sta.executeQuery();
            if (resultSet == null || resultSet.isBeforeFirst()) {
                return commentList;
            }
            while (resultSet.next()) {
                Comment comment;
                try {
                    commentList.add(new Comment(resultSet));
                } catch (CommentCreationException ex) {
                    Log.logWarning(ex.getMessage(), Comment.class);
                }
            }
        } catch (SQLException ex) {
            Log.logError(ex.getMessage(), Comment.class);
        }
        return commentList;

    }

    /**
     *
     * @param userId
     * @param commentId
     */
    public static void deleteCommentFlag(int userId, int commentId) {
        try (PreparedStatement sta = MysqlConnector.getConnection().prepareStatement("DELETE FROM commentflags WHERE " + Columns.COMMENT_ID + " = " + commentId + " AND " + Columns.USER_ID + " = " + userId)) {
            sta.executeUpdate();
        } catch (Exception ex) {
            Log.logWarning("Could not delete commentflag " + commentId + " - rootcause: " + ex, Comment.class);
        }
    }

    /**
     * Deletes the comment & related flags out of the database
     *
     * @param commentId
     * @throws java.lang.Exception
     */
    public static void deleteComment(int commentId) throws Exception {
        try (PreparedStatement sta = MysqlConnector.getConnection().prepareStatement("DELETE FROM comments WHERE " + Columns.ID + " = ?")) {
            sta.setInt(1, commentId);
            sta.executeUpdate();
        } catch (Exception ex) {
            Log.logWarning("Could not delete comment " + commentId + " - rootcause: " + ex, Comment.class);
        }

        try (PreparedStatement sta = MysqlConnector.getConnection().prepareStatement("DELETE FROM commentflags WHERE " + Columns.COMMENT_ID + " = ?")) {
            sta.setInt(1, commentId);
            sta.executeUpdate();
        } catch (Exception ex) {
            Log.logWarning("Could not delete commentflag " + commentId + " - rootcause: " + ex, Comment.class);
        }
    }
}

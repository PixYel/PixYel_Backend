/*
package pixyel_backend.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import static pixyel_backend.database.SqlUtils.listToSqlINString;
import pixyel_backend.database.objects.Comment;

public class DatabaseFunctionsComment {

    private final Connection conn;

    public DatabaseFunctionsComment() throws Exception {
        this.conn = MysqlConnector.connectToDatabaseUsingPropertiesFile();
    }

    public Comment getComment(int id) throws SQLException {
        Comment comment;
        ResultSet resultSet;
        try (PreparedStatement statement = conn.prepareStatement("SELECT * FROM comments WHERE commentid LIKE (?)")) {
            statement.setInt(0, id);
            resultSet = statement.executeQuery();
        }
        resultSet.next();

        int commentId = resultSet.getInt("commentid");
        int pictureId = resultSet.getInt("pictureid");
        int userId = resultSet.getInt("userid");
        String commentString = resultSet.getString("comment");
        Date commentDate = resultSet.getDate("comment_date");
        int flags = resultSet.getInt("flags");

        comment = new Comment(commentId, pictureId, userId, commentString, commentDate, flags);
        return comment;
    }

    public HashMap<Integer, Comment> getCommentListFromCommentId(List ids) throws SQLException {
        HashMap commentList;
        ResultSet resultSet;
        String idString = listToSqlINString(ids);
        idString = SqlUtils.escapeString(idString);
        try (PreparedStatement statement = conn.prepareStatement("SELECT * FROM comments WHERE commentid IN (?)")) {
            statement.setString(0, idString);
            resultSet = statement.executeQuery();
        }
        commentList = getCommentList(resultSet); //getCommentList creates the Hashmap
        return commentList;
    }

    public HashMap<Integer, Comment> getCommentListFromPictureId(List ids) throws SQLException {
        HashMap commentList;
        ResultSet resultSet;
        String idString = listToSqlINString(ids);
        idString = SqlUtils.escapeString(idString);
        try(PreparedStatement statement = conn.prepareStatement("SELECT * FROM comments WHERE pictureid IN (?)")){
            statement.setString(0, idString);
            resultSet = statement.executeQuery();
        }
        commentList = getCommentList(resultSet); //getCommentList creates the Hashmap
        return commentList;
    }


    public HashMap<Integer, Comment> getCommentList(ResultSet commentResults) throws SQLException {
        HashMap commentList = new HashMap();
        while (commentResults.next()) {
            int commentId = commentResults.getInt("commentid");
            int pictureId = commentResults.getInt("pictureid");
            int userId = commentResults.getInt("userid");
            String commentString = commentResults.getString("comment");
            Date commentDate = commentResults.getDate("comment_date");
            int flags = commentResults.getInt("flags");

            Comment comment = new Comment(commentId, pictureId, userId, commentString, commentDate, flags);
            commentList.put(commentId, comment);
        }
        return commentList;
    }

    public void closeConnection() {
        try {
            this.conn.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
}
*/
package pixyel_backend.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import static pixyel_backend.database.SqlUtils.listToSqlINString;
import pixyel_backend.database.objects.Comment;
import pixyel_backend.database.objects.PictureInfo;

public class DatabaseFunctions {

    private final Connection conn;
    private final Statement statements;

    public void addNewUser(String storeID) throws SQLException {
        statements.executeUpdate("INSERT INTO users(storeid)VALUES ('"
                + storeID + "')");
    }

    public DatabaseFunctions() throws Exception {
        this.conn = MysqlConnector.connectToDatabaseUsingPropertiesFile();
        this.statements = conn.createStatement();
    }
    
    /**
     * Creates a HashMap with the PicutreId as Key & all infos about the Picture in the value - except the Picturedata
     * @param ids
     * @return
     * @throws SQLException 
     */
    public HashMap<Integer, String> getPictureData(List ids) throws SQLException {
        HashMap picturesData = new HashMap();
        ResultSet resultSet;
        String idString = listToSqlINString(ids); //Convert ID-List to String for SQL compatability
        resultSet = statements.executeQuery("SELECT * FROM picturesData WHERE pictureid IN (" + idString + ")");
        //Create Hashmap from the result Set
        while (resultSet.next()) {
            picturesData.put(resultSet.getInt("pictureid"), resultSet.getString("data"));
        }
        return picturesData;
    }
    /**
     * 
     * @param ids
     * @return
     * @throws SQLException 
     */
    public HashMap<Integer, PictureInfo> getPictureInfoList(List ids) throws SQLException {
        HashMap picturesInfo = new HashMap();

        ResultSet resultSet;
        String idString = listToSqlINString(ids); //Convert ID-List to String for SQL compatability
        resultSet = statements.executeQuery("SELECT * FROM picturesInfo WHERE pictureid IN (" + idString + ")");
        //Create Hashmap from the result Set
        PictureInfo info;
        while (resultSet.next()) {
            int id = resultSet.getInt("pictureid");
            double xCoord = resultSet.getDouble("xCoords");
            double yCoord = resultSet.getDouble("yCoords");
            Date timestamp = resultSet.getDate("timestamp");
            int upvotes = resultSet.getInt("upvotes");
            int downvotes = resultSet.getInt("downvotes");
            int flags = resultSet.getInt("flags");
            int userId = resultSet.getInt("userId");
            String commentsId = resultSet.getString("commentsId");

            info = new PictureInfo(id, xCoord, yCoord, timestamp, upvotes, downvotes, flags, userId, commentsId);
            picturesInfo.put(id, info);
        }
        return picturesInfo;
    }
    
    /**
     * 
     * @param id
     * @return
     * @throws SQLException 
     */
    public Comment getComment(int id) throws SQLException {
        Comment comment;
        ResultSet resultSet;
        String idString = Integer.toString(id);
        resultSet = statements.executeQuery("SELECT * FROM comments WHERE commentid LIKE (" + idString + ")");
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
    /**
     * 
     * @param ids
     * @return
     * @throws SQLException 
     */
    public HashMap<Integer, Comment> getCommentListFromCommentId(List ids) throws SQLException {
        HashMap commentList;
        ResultSet resultSet;
        String idString = listToSqlINString(ids);
        resultSet = statements.executeQuery("SELECT * FROM comments WHERE commentid IN (" + idString + ")");
        commentList = getCommentList(resultSet); //getCommentList creates the Hashmap
        return commentList;
    }
    
    /**
     * 
     * @param ids
     * @return
     * @throws SQLException 
     */
    public HashMap<Integer, Comment> getCommentListFromPictureId(List ids) throws SQLException {
        HashMap commentList;
        ResultSet resultSet;
        String idString = listToSqlINString(ids);
        resultSet = statements.executeQuery("SELECT * FROM comments WHERE pictureid IN (" + idString + ")");
        commentList = getCommentList(resultSet); //getCommentList creates the Hashmap
        return commentList;
    }

    /**
     *
     * @param commentResults
     * @return
     * @throws java.sql.SQLException
     */
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
            this.statements.close();
            this.conn.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
}

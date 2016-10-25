package pixyel_backend.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import static pixyel_backend.database.SqlUtils.listToSqlINString;
import pixyel_backend.database.objects.PictureInfo;

/**
 *
 * @author Da_Groove
 */
public class DatabaseFunctionsPicture {

    private final Connection conn;
    
    public DatabaseFunctionsPicture() throws Exception{
        this.conn=MysqlConnector.connectToDatabaseUsingPropertiesFile();
    }
    
    /**
     * Creates a HashMap with the PicutreId as Key & all infos about the Picture
     * in the value - except the Picturedata
     *
     * @param ids
     * @return
     * @throws SQLException
     */
    public HashMap<Integer, String> getPictureData(List ids) throws SQLException {
        HashMap picturesData = new HashMap();
        ResultSet resultSet;
        String idString = listToSqlINString(ids); //Convert ID-List to String for SQL compatability
        try (PreparedStatement statement = conn.prepareStatement("SELECT * FROM picturesData WHERE pictureid IN (?)")) {
            statement.setString(0, idString);
            resultSet = statement.executeQuery();
        }
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
        try (PreparedStatement statement = conn.prepareStatement("SELECT * FROM picturesInfo WHERE pictureid IN (?)")) {
            statement.setString(0, idString);
            resultSet = statement.executeQuery();
        }
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
    
    public void closeConnection() {
        try {
            this.conn.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

}

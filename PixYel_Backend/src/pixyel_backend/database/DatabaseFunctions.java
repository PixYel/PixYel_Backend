package pixyel_backend.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import static pixyel_backend.database.SqlUtils.listToSqlINString;
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

    public HashMap<Integer, PictureInfo> getPictureInfo(List ids) throws SQLException {
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

    public void closeConnection() {
        try {
            this.statements.close();
            this.conn.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
}

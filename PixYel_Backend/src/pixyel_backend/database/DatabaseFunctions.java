package pixyel_backend.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import static pixyel_backend.database.SqlUtils.listToSqlINString;

public class DatabaseFunctions {
	private Connection conn;
	private Statement statements;

	public DatabaseFunctions() throws Exception {
		this.conn = MysqlConnector.connectToDatabaseUsingPropertiesFile();
		this.statements = conn.createStatement();
	}

	public void addNewUser(int phonenumber, String deviceId) throws SQLException {
		statements.executeUpdate("INSERT INTO users(phonenumber,deviceId)VALUES ('"
				+phonenumber + "','" + SqlUtils.escapeString(deviceId)+"')");
	}
        
    /**
     *
     * @param id
     * @return
     */
    public HashMap<Integer, String> getPictureData(List ids) throws SQLException{
            HashMap picturesData = new HashMap();
            ResultSet resultSet;
            String idString = listToSqlINString(ids); //Convert ID-List to String for SQL compatability
            resultSet = statements.executeQuery("SELECT * FROM picturesData WHERE pictureid IN ("+idString+")");
            //Create Hashmap from the result Set
            while(resultSet.next()){ 
                picturesData.put(resultSet.getInt("pictureid"), resultSet.getString("data"));
            }
            return picturesData;
        }
        
	public void closeConnection() {
		try {
			this.statements.close();
			this.conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}

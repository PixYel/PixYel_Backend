package pixyel_backend.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;

public class DatabaseFunctions {
	private Connection conn;
	private Statement statments;

	public DatabaseFunctions() throws Exception {
		this.conn = MysqlConnector.connectToDatabaseUsingPropertiesFile();
		this.statments = conn.createStatement();
	}

	public void addNewUser(String phonenumber, String deviceId) throws SQLException {

		java.util.Date date = new Date();
		Timestamp timestamp = new Timestamp(date.getTime());
		statments.executeUpdate("INSERT INTO newUsers(phonenumber,deviceId,reg_date)VALUES ("
				+ SqlUtils.escapeString(phonenumber) + "," + SqlUtils.escapeString(deviceId) + "," + timestamp + ")");
	}

	public void closeConnection() {
		try {
			this.statments.close();
			this.conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}

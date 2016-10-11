package pods_backend.database;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * @author Yannick
 *
 */
public class MysqlConnector{

	/**
	 * Creates a connection to a database
	 * @param host
	 * @param database
	 * @param user
	 * @param passwd
	 * @return The Connection
	 * @throws Exception
	 */
	
	public static Connection connectToDatabase(String host, String database, String user, String passwd) throws Exception {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			String connectionCommand = "jdbc:mysql://" + host + "/" + database + "?user=" + user + "&password="
					+ passwd;
			Connection connection = DriverManager.getConnection(connectionCommand);
			return connection;
		} catch (Exception ex) {
			throw new Exception("Cant connect to Db \n rootcause : " + ex);
		}
	}
}
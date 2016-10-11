package pods_backend.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

/**
 * @author Yannick
 *
 */
public class MysqlConnector {

	/**
	 * Creates a connection to a database
	 * 
	 * @param host
	 * @param database
	 * @param user
	 * @param passwd
	 * @return The Connection
	 * @throws Exception
	 */

	public static Connection connectToDatabase(String host, String database, String user, String passwd)
			throws Exception {
		try {
			return getConnection(host, database, user, passwd);
		} catch (Exception ex) {
			throw new Exception("Cant connect to Db \n rootcause : " + ex);
		}
	}

	private static Connection getConnection(String host, String database, String user, String passwd) throws Exception {
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		String connectionCommand = "jdbc:mysql://" + host + "/" + database + "?user=" + user + "&password=" + passwd;
		Connection connection = DriverManager.getConnection(connectionCommand);
		return connection;
	}

	/**
	 * Creates a connection to a database
	 */
	public static Connection connectToDatabaseUsingPropertiesFile() throws Exception {
		try {
			Properties properties = DbAccessPropertiesReader.getProperties();
			String host = properties.getProperty("mysqlhost");
			String database = properties.getProperty("mysqldatabase");
			String user = properties.getProperty("mysqluser");
			String passwd = properties.getProperty("mysqlpassword");
			return getConnection(host, database, user, passwd);
		} catch (Exception ex) {
			throw new Exception("Cant connect to Db \n rootcause : " + ex);
		}
	}
}
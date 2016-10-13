package pixyel_backend.database;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {
	public static void init() throws Exception {
		Connection conn = MysqlConnector.connectToDatabaseUsingPropertiesFile();
		Statement statments = conn.createStatement();
		statments.executeUpdate("DROP DATABASE IF EXISTS pixdb");
		statments.executeUpdate("CREATE DATABASE pixdb");
		statments.executeUpdate("USE pixdb");
		statments.executeUpdate(
				"CREATE TABLE users (userid INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY, phonenumber VARCHAR(30) NOT NULL UNIQUE, deviceId VARCHAR(30) NOT NULL UNIQUE,reg_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,status TINYINT(1) DEFAULT '0',amountsmssend INT(5) DEFAULT '0')");
		statments.executeUpdate(
				"CREATE TABLE picturesInfo (pictureid INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY, geodata Varchar(30), upvotes int(6), downvotes int(6))");
		statments.executeUpdate("CREATE TABLE picturesData (pictureid INT(6) PRIMARY KEY, data LONGTEXT NOT NULL)");
	}
}
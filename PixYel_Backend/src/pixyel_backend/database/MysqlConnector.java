package pixyel_backend.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import pixyel_backend.database.exceptions.DbConnectionException;

/**
 * @author Yannick
 *
 */
public class MysqlConnector {

    /**
     * Connection to a database 
     * Default is a connection to the Productiv db which was set up by using the
     * properties file
     */
    private static Connection CONNECTION = MysqlConnector.connectToProductivDatabaseUsingPropertiesFile();

    
    public static Connection getConnection() {
        return CONNECTION;
    }

    public static void useTestDB() {
        CONNECTION = MysqlConnector.connectToTestDatabaseUsingPropertiesFile();
    }

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
     *
     * @return
     */
    public static Connection connectToProductivDatabaseUsingPropertiesFile() {
        try {
            Properties properties = DbAccessPropertiesReader.getProperties();
            String host = properties.getProperty("mysqlhost");
            String database = properties.getProperty("mysqlproductivdatabase");
            String user = properties.getProperty("mysqluser");
            String passwd = properties.getProperty("mysqlpassword");
            return getConnection(host, database, user, passwd);
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return null;
    }

    public static Connection connectToTestDatabaseUsingPropertiesFile() {
        try {
            Properties properties = DbAccessPropertiesReader.getProperties();
            String host = properties.getProperty("mysqlhost");
            String database = properties.getProperty("mysqltestdatabase");
            String user = properties.getProperty("mysqluser");
            String passwd = properties.getProperty("mysqlpassword");
            return getConnection(host, database, user, passwd);
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return null;
    }

    /**
     * Creates a connection to the databasesystem without using a speciall
     * Database
     *
     * @return
     * @throws ConnectionException if can not connect to database
     */
    public static Connection connectToDatabaseSystem() throws DbConnectionException {
        try {
            Properties properties = DbAccessPropertiesReader.getProperties();
            String host = properties.getProperty("mysqlhost");
            String user = properties.getProperty("mysqluser");
            String passwd = properties.getProperty("mysqlpassword");
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            String connectionCommand = "jdbc:mysql://" + host + "?user=" + user + "&password=" + passwd;
            Connection connection = DriverManager.getConnection(connectionCommand);
            return connection;
        } catch (Exception ex) {
            System.out.println(ex);
            throw new DbConnectionException();
        }
    }
}

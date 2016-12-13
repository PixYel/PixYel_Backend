package pixyel_backend.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import pixyel_backend.database.exceptions.DbConnectionException;

/**
 * @author Yannick
 */
public class MysqlConnector {

    /**
     * Connection to a database Default is a connection to the Productiv db
     * which was set up by using the properties file
     */
    private static Connection CONNECTION = MysqlConnector.connectToProductivDatabaseUsingPropertiesFile();
    
    /**
     * Returns the Connection pixyel_backend.database.MysqlConnector.CONNECTION
     *
     * @return
     */
    public static Connection getConnection() {
        return CONNECTION;
    }

    /**
     * Changes the used database to the test-database
     */
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
            return openConnection(host, database, user, passwd);
        } catch (ClassNotFoundException | InstantiationException | SQLException | IllegalAccessException ex) {
            throw new Exception("Cant connect to Db \n rootcause : " + ex);
        }
    }

    /**
     * Creates a Connection to a database by using the given parameters
     *
     * @param host
     * @param database
     * @param user
     * @param passwd
     * @return The connection that was opened
     * @throws Exception
     */
    private static Connection openConnection(String host, String database, String user, String passwd) throws ClassNotFoundException, InstantiationException, SQLException, IllegalAccessException {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        String connectionCommand = "jdbc:mysql://" + host + "/" + database + "?user=" + user + "&password=" + passwd + "&autoReconnect=true&useUnicode=yes&useSSL=true";
        Connection connection = DriverManager.getConnection(connectionCommand);
        return connection;
    }

    /**
     * Creates a connection to the productiv-database by using the properties
     * given by pixyel_backend.database.DbAccessPropertiesReader.getProperties()
     *
     * @return a Connection to the productiv-database
     */
    public static Connection connectToProductivDatabaseUsingPropertiesFile() {
        try {
            Properties properties = DbAccessPropertiesReader.getProperties();
            String host = properties.getProperty("mysqlhost");
            String database = properties.getProperty("mysqlproductivdatabase");
            String user = properties.getProperty("mysqluser");
            String passwd = properties.getProperty("mysqlpassword");
            return openConnection(host, database, user, passwd);
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return null;
    }

    /**
     * Creates a connection to the test-database by using the properties given
     * by pixyel_backend.database.DbAccessPropertiesReader.getProperties()
     *
     * @return a Connection to the test-database
     */
    public static Connection connectToTestDatabaseUsingPropertiesFile() {
        try {
            Properties properties = DbAccessPropertiesReader.getProperties();
            String host = properties.getProperty("mysqlhost");
            String database = properties.getProperty("mysqltestdatabase");
            String user = properties.getProperty("mysqluser");
            String passwd = properties.getProperty("mysqlpassword");
            return openConnection(host, database, user, passwd);
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return null;
    }

    /**
     * Creates a connection to the databasesystem without using a speciall
     * database
     *
     * @return
     * @throws DbConnectionException if can not connect to database
     */
    public static Connection connectToDatabaseSystemUsing() throws DbConnectionException {
        try {
            Properties properties = DbAccessPropertiesReader.getProperties();
            String host = properties.getProperty("mysqlhost");
            String user = properties.getProperty("mysqluser");
            String passwd = properties.getProperty("mysqlpassword");
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            String connectionCommand = "jdbc:mysql://" + host + "?user=" + user + "&password=" + passwd + "&autoReconnect=true&useUnicode=yes";
            Connection connection = DriverManager.getConnection(connectionCommand);
            return connection;
        } catch (Exception ex) {
            System.out.println(ex);
            throw new DbConnectionException();
        }
    }
}

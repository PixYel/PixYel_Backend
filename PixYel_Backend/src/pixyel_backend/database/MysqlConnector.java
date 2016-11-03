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
     * @throws DbConnectionException if can not connect to database
     */
    public static Connection connectToProductivDatabaseUsingPropertiesFile() throws DbConnectionException {
        try {
            Properties properties = DbAccessPropertiesReader.getProperties();
            String host = properties.getProperty("mysqlhost");
            String database = properties.getProperty("mysqlproductivdatabase");
            String user = properties.getProperty("mysqluser");
            String passwd = properties.getProperty("mysqlpassword");
            return getConnection(host, database, user, passwd);
        } catch (Exception ex) {
            System.out.println(ex);
            throw new DbConnectionException();
        }
    }

    public static Connection connectToTestDatabaseUsingPropertiesFile() throws DbConnectionException {
        try {
            Properties properties = DbAccessPropertiesReader.getProperties();
            String host = properties.getProperty("mysqlhost");
            String database = properties.getProperty("mysqltestdatabase");
            String user = properties.getProperty("mysqluser");
            String passwd = properties.getProperty("mysqlpassword");
            return getConnection(host, database, user, passwd);
        } catch (Exception ex) {
            System.out.println(ex);
            throw new DbConnectionException();
        }
    }

    /**
     * Creates a connection to the databasesystem without using a speciall
     * Database
     *
     * @return
     * @throws DbConnectionException if can not connect to database
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

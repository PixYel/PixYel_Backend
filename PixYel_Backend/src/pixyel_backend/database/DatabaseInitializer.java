package pixyel_backend.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import pixyel_backend.Log;
import pixyel_backend.database.exceptions.DbConnectionException;

public class DatabaseInitializer {

    /**
     * Initializes the whole productiv db 
     * @throws SQLException
     * @throws ConnectionException
     * @throws Exception 
     */
    public static void initProductivDatabase() throws SQLException, DbConnectionException, Exception {
        Properties properties = DbAccessPropertiesReader.getProperties();
        String databaseName = properties.getProperty("mysqlproductivdatabase");
        Log.logInfo("Database-Initialization: starting database-initialization", DatabaseInitializer.class);
        Log.logInfo("Database-Initialization: setting up Connection to the database", DatabaseInitializer.class);
        Connection con = MysqlConnector.connectToProductivDatabaseUsingPropertiesFile();
        runInit(databaseName, con);
        Log.logInfo("finished database-initialization", DatabaseInitializer.class);
    }

    /**
     * Initializes the whole test db 
     * @throws SQLException
     * @throws ConnectionException
     * @throws Exception 
     */
    public static void initTestDatabase() throws SQLException, DbConnectionException, Exception {
        Properties properties = DbAccessPropertiesReader.getProperties();
        String databaseName = properties.getProperty("mysqltestdatabase");
        Log.logInfo("Database-Initialization: starting testdatabase-initialization", DatabaseInitializer.class);
        Log.logInfo("Database-Initialization: setting up Connection to the database", DatabaseInitializer.class);
        Connection con = MysqlConnector.connectToProductivDatabaseUsingPropertiesFile();
        runInit(databaseName, con);
        Log.logInfo("finished database-initialization", DatabaseInitializer.class);
    }

    /**
     * Initializes the given database
     * @param databaseName
     * @param con
     * @throws SQLException 
     */
    private static void runInit(String databaseName, Connection con) throws SQLException {
        try (Statement statements = con.createStatement()) {
            Log.logInfo("Database-Initialization: deleting old Database", DatabaseInitializer.class);
            statements.executeUpdate("DROP DATABASE IF EXISTS " + databaseName);
            
            Log.logInfo("Database-Initialization: creating new Databse", DatabaseInitializer.class);
            statements.executeUpdate("CREATE DATABASE " + databaseName);
            statements.executeUpdate("USE " + databaseName);
            
            statements.executeUpdate("CREATE TABLE users ("
                    + "id               INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY, "
                    + "storeid          VARCHAR(80) NOT NULL UNIQUE,"
                    + "reg_date         TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                    + "publickey        TEXT, "
                    + "status           TINYINT(1) DEFAULT '0')"
            );
            
            statements.executeUpdate("CREATE TABLE picturesInfo ("
                    + "pictureid        INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY, "
                    + "longitude        DOUBLE NOT NULL, "
                    + "latitude         DOUBLE NOT NULL, "
                    + "upload_date      TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                    + "upvotes          INT(6) DEFAULT '0', "
                    + "downvotes        INT(6) DEFAULT '0', "
                    + "flags            TINYTEXT, "
                    + "userid           INT(6) NOT NULL, "
                    + "commentids       TINYTEXT)"
            );
            
            statements.executeUpdate("CREATE TABLE picturesData ("
                    + "pictureid        INT(6) PRIMARY KEY, "
                    + "data             LONGTEXT NOT NULL)"
            );
            
            statements.executeUpdate("CREATE TABLE comments("
                    + "commentid        INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY, "
                    + "pictureid        INT(6) NOT NULL, "
                    + "userid           INT(6) NOT NULL, "
                    + "comment          VARCHAR(1200) NOT NULL, "
                    + "comment_date     TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                    + "flags            TINYTEXT)"
            );
        }
    }
}

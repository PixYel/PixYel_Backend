package pixyel_backend.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import pixyel_backend.Log;
import pixyel_backend.database.exceptions.DbConnectionException;
import pixyel_backend.database.exceptions.UserCreationException;
import pixyel_backend.database.objects.WebUser;

public class DatabaseInitializer {

    /**
     * Initializes the whole productiv db
     *
     * @throws SQLException
     * @throws pixyel_backend.database.exceptions.DbConnectionException
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
     *
     * @throws SQLException
     * @throws DbConnectionException
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
     *
     * @param databaseName
     * @param con
     * @throws SQLException
     */
    private static void runInit(String databaseName, Connection con) throws SQLException {
        try (Statement statements = con.createStatement()) {
            Log.logInfo("Database-Initialization: deleting old Database - " + databaseName, DatabaseInitializer.class);
            statements.executeUpdate("DROP DATABASE IF EXISTS " + databaseName);

            Log.logInfo("Database-Initialization: creating new Databse", DatabaseInitializer.class);
            statements.executeUpdate("CREATE DATABASE " + databaseName);
            statements.executeUpdate("USE " + databaseName);

            statements.executeUpdate("CREATE TABLE users ("
                    + Columns.ID + " INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY, "
                    + Columns.STORE_ID + " VARCHAR(80) NOT NULL UNIQUE,"
                    + Columns.REGISTRATION_DATE + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                    + Columns.PUBLICKEY + " TEXT, "
                    + Columns.STATUS + " TINYINT(1) DEFAULT '0')"
            );
            
            statements.executeUpdate("CREATE TABLE webusers ("
                    + Columns.ID + " INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,"
                    + Columns.NAME + " VARCHAR(30) NOT NULL UNIQUE,"
                    + Columns.PW + " VARCHAR(300) NOT NULL,"
                    + Columns.REGISTRATION_DATE + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP) "
            );
            
            WebUser.addNewWebUser("Admin", "nimda");

            statements.executeUpdate("CREATE TABLE picturesInfo ("
                    + Columns.ID + " INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY, "
                    + Columns.LONGITUDE + " DOUBLE NOT NULL, "
                    + Columns.LATITUDE + " DOUBLE NOT NULL, "
                    + Columns.UPLOAD_DATE + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                    + Columns.USER_ID + " INT(6) NOT NULL)"
            );

            statements.executeUpdate("CREATE TABLE picturesVotes ("
                    + Columns.PICTURE_ID + " INT(6) NOT NULL, "
                    + Columns.USER_ID + " INT(6) NOT NULL, "
                    + Columns.STATUS + " TINYINT(1) NOT NULL)" //1 für Upvote -1 für downvote 0 for no vote
            );
            statements.executeUpdate("CREATE UNIQUE INDEX id ON picturesVotes (" + Columns.PICTURE_ID + "," + Columns.USER_ID + ")");

            statements.executeUpdate("CREATE TABLE picturesData ("
                    + Columns.PICTURE_ID + " INT(6) PRIMARY KEY, "
                    + Columns.DATA + " LONGTEXT NOT NULL)"
            );

            statements.executeUpdate("CREATE TABLE comments("
                    + Columns.ID + " INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY, "
                    + Columns.PICTURE_ID + " INT(6) NOT NULL, "
                    + Columns.USER_ID + " INT(6) NOT NULL, "
                    + Columns.TEXT + " VARCHAR(1200) NOT NULL, "
                    + Columns.CREATION_DATE + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP)"
            );

            statements.executeUpdate("CREATE TABLE pictureflags("
                    + Columns.PICTURE_ID + " INT(6) NOT NULL, "
                    + Columns.USER_ID + " INT(6) NOT NULL, "
                    + Columns.CREATION_DATE + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP) "
            );
            statements.executeUpdate("CREATE UNIQUE INDEX id ON pictureflags (" + Columns.PICTURE_ID + "," + Columns.USER_ID + ")");

            statements.executeUpdate("CREATE TABLE commentflags("
                    + Columns.COMMENT_ID + " INT(6) NOT NULL, "
                    + Columns.USER_ID + " INT(6) NOT NULL, "
                    + Columns.CREATION_DATE + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP) "
            );
            statements.executeUpdate("CREATE UNIQUE INDEX id ON commentflags (" + Columns.COMMENT_ID + "," + Columns.USER_ID + ")");
        } catch (UserCreationException ex) {
            Log.logError(ex.getMessage(), DatabaseInitializer.class);
        }
    }
}

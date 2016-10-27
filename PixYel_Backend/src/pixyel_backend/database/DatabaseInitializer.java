package pixyel_backend.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import pixyel_backend.Log;
import pixyel_backend.database.exceptions.DbConnectionException;

public class DatabaseInitializer {

    public static void init() throws SQLException, DbConnectionException {
        Log.logInfo("Database-Initialization: starting database-initialization", DatabaseInitializer.class);
        Log.logInfo("Database-Initialization: setting up Connection to the database", DatabaseInitializer.class);
        Connection conn;
        conn = MysqlConnector.connectToDatabaseUsingPropertiesFile();
        Statement statements;
        statements = conn.createStatement();
        
        Log.logInfo("Database-Initialization: deleting old Database", DatabaseInitializer.class);
        statements.executeUpdate("DROP DATABASE IF EXISTS pixdb");
        
        Log.logInfo("Database-Initialization: creating new Databse", DatabaseInitializer.class);
        statements.executeUpdate("CREATE DATABASE pixdb");
        statements.executeUpdate("USE pixdb");

        statements.executeUpdate("CREATE TABLE users ("
                + "id               INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY, "
                + "storeid          VARCHAR(80) NOT NULL UNIQUE,"
                + "reg_date         TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                + "publickey        TEXT, "
                + "status           TINYINT(1) DEFAULT '0')"
        );

        statements.executeUpdate("CREATE TABLE picturesInfo ("
                + "pictureid        INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY, "
                + "x_Coordinate     DOUBLE NOT NULL, "
                + "y_coordinate     DOUBLE NOT NULL, "
                + "upload_date      TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                + "upvotes          INT(6) DEFAULT '0', "
                + "downvotes        INT(6) DEFAULT '0', "
                + "flags            TINYTEXT, "
                + "userid           INT(6) NOT NULL, "
                + "commentIds       TINYTEXT)"
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
                + "flags            TINYTEXT"
        );
        
        statements.close();
        conn.close();
        Log.logInfo("finished database-initialization", DatabaseInitializer.class);
    }
}


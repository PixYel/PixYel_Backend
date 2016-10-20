package pixyel_backend.database;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void init() throws Exception {
        Connection conn = MysqlConnector.connectToDatabaseUsingPropertiesFile();
        Statement statements = conn.createStatement();
        statements.executeUpdate("DROP DATABASE IF EXISTS pixdb");
        statements.executeUpdate("CREATE DATABASE pixdb");
        statements.executeUpdate("USE pixdb");

        statements.executeUpdate("CREATE TABLE users ("
                + "id               INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY, "
                + "phonenumber      INT(30) NOT NULL UNIQUE, "
                + "deviceId         VARCHAR(80) NOT NULL UNIQUE,"
                + "reg_date         TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                + "publickey        TEXT, "
                + "status           TINYINT(1) DEFAULT '0',"
                + "amountsmssend    INT(5) DEFAULT '0')"
        );

        statements.executeUpdate("CREATE TABLE picturesInfo ("
                + "pictureid        INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY, "
                + "x_Coordinate     DOUBLE NOT NULL, "
                + "y_coordinate     DOUBLE NOT NULL, "
                + "upload_date      TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                + "upvotes          INT(6) DEFAULT '0', "
                + "downvotes        INT(6) DEFAULT '0', "
                + "flags            INT(6) DEFAULT '0', "
                + "userid           INT(6) NOT NULL, "
                + "commentId        MEDIUMTEXT)"
        );

        statements.executeUpdate("CREATE TABLE picturesData ("
                + "pictureid        INT(6) PRIMARY KEY, "
                + "data             LONGTEXT NOT NULL)"
        );
        
        statements.executeUpdate("CREATE TABLE comments("
                + "commentid        INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY, "
                + "pictureid        INT(6) NOT NULL, "
                + "userid           INT(6) NOT NULL, "
                + "comment          VARCHAR NOT NULL, "
                + "comment_date     TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                + "flags            INT(6) DEFAULT '0')"
        );
        
        statements.close();
        conn.close();
    }
}


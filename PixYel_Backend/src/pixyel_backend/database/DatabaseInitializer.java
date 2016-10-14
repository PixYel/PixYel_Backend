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
                + "userid INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY, "
                + "phonenumber INT(30) NOT NULL UNIQUE, "
                + "deviceId VARCHAR(80) NOT NULL UNIQUE,"
                + "reg_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                + "publickey TEXT, "
                + "status TINYINT(1) DEFAULT '0',"
                + "amountsmssend INT(5) DEFAULT '0')"
        );

        statements.executeUpdate("CREATE TABLE picturesInfo ("
                + "pictureid INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY, "
                + "x_Coordinate DOUBLE, "
                + "y_coordinate DOUBLE, "
                + "upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                + "upvotes INT(6), "
                + "downvotes INT(6), "
                + "flags INT(6), "
                + "userid INT(6), "
                + "commentId MEDIUMTEXT)"
        );

        statements.executeUpdate("CREATE TABLE picturesData ("
                + "pictureid INT(6) PRIMARY KEY, "
                + "data LONGTEXT NOT NULL)"
        );
        statements.close();
        conn.close();
    }
}


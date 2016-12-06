/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend.database.dataProcessing;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import pixyel_backend.Log;
import pixyel_backend.database.Columns;
import pixyel_backend.database.MysqlConnector;

/**
 *
 * @author Yannick
 */
public class Statistics {

    /**
     * 
     * @return the amount of registered user
     */
    public static int countRegisteredUsers() {
        try (Statement sta = MysqlConnector.getConnection().createStatement(); ResultSet result = sta.executeQuery("SELECT COUNT(*) AS NumberOfUser FROM users;")) {
            result.next();
            return result.getInt(1);
        } catch (SQLException ex) {
            Log.logError(ex.getMessage(), Statistics.class);
            return -1;
        }
    }

    /**
     * 
     * @return the amount of banned users
     */
    public static int countBannedUsers() {
        try (Statement sta = MysqlConnector.getConnection().createStatement(); ResultSet result = sta.executeQuery("SELECT COUNT(*) AS NumberOfUser FROM users WHERE " + Columns.STATUS + " < 0;")) {
            result.next();
            return result.getInt(1);
        } catch (SQLException ex) {
            Log.logError(ex.getMessage(), Statistics.class);
            return -1;
        }
    }

    /**
     * 
     * @return the amount of all pictures that were posted
     */
    public static int totallyPostedPictures() {
        try (Statement sta = MysqlConnector.getConnection().createStatement(); ResultSet result = sta.executeQuery("SELECT COUNT(*) AS NumberOfPictures FROM picturesInfo;")) {
            result.next();
            return result.getInt(1);
        } catch (SQLException ex) {
            Log.logError(ex.getMessage(), Statistics.class);
            return -1;
        }
    }
    
    

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend.database.dataProcessing;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
    public static int totallyRegisteredUsers() {
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
    public static int totallyBannedUsers() {
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
    public static int totallyUploadedPictures() {
        try (Statement sta = MysqlConnector.getConnection().createStatement(); ResultSet result = sta.executeQuery("SELECT COUNT(*) AS NumberOfPictures FROM picturesInfo;")) {
            result.next();
            return result.getInt(1);
        } catch (SQLException ex) {
            Log.logError(ex.getMessage(), Statistics.class);
            return -1;
        }
    }

    /**
     * Counts how many new Users were registrated within the last 24 hours
     *
     * @return
     */
    public int UserRegistrationInTheLastDay() {
        return UserRegistrationInTheLastTime(1);
    }

    /**
     * Counts how many new Users were registrated within the last week
     *
     * @return
     */
    public int UserRegistrationInTheLast7Days() {
        return UserRegistrationInTheLastTime(7);
    }

    /**
     * Counts how many new Users were registrated within the last week
     *
     * @return
     */
    public int UserRegistrationInTheLast30Days() {
        return UserRegistrationInTheLastTime(30);
    }

    /**
     * Counts how many new Users were registrated within a given time
     *
     * @param days
     * @return
     */
    private int UserRegistrationInTheLastTime(int days) {
        Instant instant = Instant.now().minus(days, ChronoUnit.DAYS);
        Timestamp currentTimestamp = Timestamp.from(instant);
        try (Statement sta = MysqlConnector.getConnection().createStatement(); ResultSet result = sta.executeQuery("SELECT COUNT(*) AS NumberOfPictures FROM users WHERE " + Columns.REGISTRATION_DATE + " > " + currentTimestamp)) {
            result.next();
            return result.getInt(1);
        } catch (SQLException ex) {
            Log.logError(ex.getMessage(), Statistics.class);
            return -1;
        }
    }

    /**
     * Counts how many new Pictures were uploaded within the last week
     *
     * @return
     */
    public int PictureUploadsInTheLastDay() {
        return PictureUploadsInTheLastTime(1);
    }

    /**
     * Counts how many new Pictures were uploaded within the last week
     *
     * @return
     */
    public int PictureUploadsInTheLast7Days() {
        return PictureUploadsInTheLastTime(7);
    }

    /**
     * * Counts how many new Pictures were uploaded within the last week
     *
     * @return
     */
    public int PictureUploadsInTheLast30Days() {
        return PictureUploadsInTheLastTime(30);
    }

    /**
     * Counts how many new Pictures were uploaded within a given time
     *
     * @param days
     * @return
     */
    private int PictureUploadsInTheLastTime(int days) {
        Instant instant = Instant.now().minus(days, ChronoUnit.DAYS);
        Timestamp currentTimestamp = Timestamp.from(instant);
        try (Statement sta = MysqlConnector.getConnection().createStatement(); ResultSet result = sta.executeQuery("SELECT COUNT(*) AS NumberOfPictures FROM picturesInfo WHERE " + Columns.UPLOAD_DATE + " > " + currentTimestamp)) {
            result.next();
            return result.getInt(1);
        } catch (SQLException ex) {
            Log.logError(ex.getMessage(), Statistics.class);
            return -1;
        }
    }
}

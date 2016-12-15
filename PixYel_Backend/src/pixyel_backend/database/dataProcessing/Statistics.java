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
    public static int countUploadedPictures() {
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
    public int UserRegistrationsDuringTheLastDay() {
        return UserRegistrationsDuringTheLastTime(1);
    }

    /**
     * Counts how many new Users were registrated within the last week
     *
     * @return
     */
    public int UserRegistrationsDuringTheLast7Days() {
        return UserRegistrationsDuringTheLastTime(7);
    }

    /**
     * Counts how many new Users were registrated within the last week
     *
     * @return
     */
    public int UserRegistrationsDuringTheLast30Days() {
        return UserRegistrationsDuringTheLastTime(30);
    }

    /**
     * Counts how many new Users were registrated within a given time
     *
     * @param days
     * @return
     */
    private int UserRegistrationsDuringTheLastTime(int days) {
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
    public int PictureUploadsDuringTheLastDay() {
        return PictureUploadsDuringTheLastTime(1);
    }

    /**
     * Counts how many new Pictures were uploaded within the last week
     *
     * @return
     */
    public int PictureUploadsDuringTheLast7Days() {
        return PictureUploadsDuringTheLastTime(7);
    }

    /**
     * * Counts how many new Pictures were uploaded within the last week
     *
     * @return
     */
    public int PictureUploadsDuringTheLast30Days() {
        return PictureUploadsDuringTheLastTime(30);
    }

    /**
     * Counts how many new Pictures were uploaded within a given time
     *
     * @param days
     * @return
     */
    private int PictureUploadsDuringTheLastTime(int days) {
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

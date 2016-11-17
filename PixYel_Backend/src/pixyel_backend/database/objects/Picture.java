/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend.database.objects;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import pixyel_backend.Log;
import pixyel_backend.database.MysqlConnector;
import pixyel_backend.database.SqlUtils;
import pixyel_backend.database.exceptions.FlagFailedExcpetion;
import pixyel_backend.database.exceptions.PictureLoadException;
import pixyel_backend.database.exceptions.PictureUploadExcpetion;

/**
 * @author Da_Groove
 */
public class Picture {

    private final int id;
    private String data;
    private int ranking;
    private final double longitude;
    private final double latitude;
    private final Coordinate coordinate;
    private final Date timestamp;
    private final int upvotes;
    private final int downvotes;
    private final int userId;

    public Picture(int pictureId) throws PictureLoadException {
        this.id = pictureId;
        try {
            //get Info
            PreparedStatement sta = MysqlConnector.getConnection().prepareStatement("SELECT * FROM picturesInfo WHERE id LIKE ?");
            sta.setInt(1, this.id);
            ResultSet result = sta.executeQuery();
            result.next();
            this.longitude = result.getDouble("longitude");
            this.latitude = result.getDouble("latitude");
            this.timestamp = result.getDate("upload_date");
            this.upvotes = result.getInt("upvotes");
            this.downvotes = result.getInt("downvotes");
            this.userId = result.getInt("userid");
        } catch (Exception ex) {
            Log.logWarning("couldnt load picture for Id " + pictureId + "- rootcause:" + ex, Picture.class);
            throw new PictureLoadException();
        }
        this.coordinate = new Coordinate(this.longitude, this.latitude);
    }
    
    public static synchronized Picture addNewPicture(int userId, String pictureData, double longitude, double latitude) throws PictureUploadExcpetion, PictureLoadException {
        int pictureId;
        try {
            Connection con = MysqlConnector.getConnection();
            PreparedStatement statement;
            if (pictureData != null && pictureData.length() >= 1) {
                statement = con.prepareStatement("INSERT INTO picturesInfo (longitude, latitude, userid,flags) VALUES(?,?,?,'')");
                statement.setDouble(1, longitude);
                statement.setDouble(2, latitude);
                statement.setInt(3, userId);
                statement.executeUpdate();
                ResultSet rs = statement.executeQuery("SELECT MAX(id) as maxid FROM picturesInfo");
                //get Id as reference for the picturedata
                rs.next();
                pictureId = rs.getInt("maxid");
                pictureData = SqlUtils.escapeString(pictureData);
                statement = con.prepareStatement("INSERT INTO picturesData (pictureid, data) VALUES(?,?)");
                statement.setInt(1, pictureId);
                statement.setString(2, pictureData);
                statement.execute();
                statement.close();
            } else {
                Log.logInfo("Failed to add picture for user \"" + userId + "\" - rootcause: picturedata is NULL or to empty string", Picture.class);
                throw new PictureUploadExcpetion();
            }
        } catch (SQLException ex) {
            Log.logError("Could not add new picture to database - rootcause: " + ex.getMessage(), Picture.class);
            throw new PictureUploadExcpetion();
        }
        return new Picture(pictureId);
    }
    
    /**
     * Adds a flag
     *
     * @param userId
     * @param pictureId
     * @throws FlagFailedExcpetion
     */
    public static synchronized void flagPic(int userId, int pictureId) throws FlagFailedExcpetion {
        try (PreparedStatement statement = MysqlConnector.getConnection().prepareStatement("SELECT id FROM  pictureflags WHERE pictureid = ? AND  userid = ?")) {
            statement.setInt(1, pictureId);
            statement.setInt(2, userId);
            ResultSet result = statement.executeQuery();
            if (result == null || !result.isBeforeFirst()) {
                try (PreparedStatement instertStatement = MysqlConnector.getConnection().prepareStatement("INSERT INTO pictureflags (pictureid,userid) VALUES (?,?)")) {
                    instertStatement.setInt(1, pictureId);
                    instertStatement.setInt(2, userId);
                    instertStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            Log.logWarning("Failed to flag picture " + pictureId + " rootcause: - " + ex, Comment.class);
            throw new FlagFailedExcpetion();

        }
    }

    public static Picture getPictureById(int id) throws PictureLoadException {
        return new Picture(id);
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @return the data
     */
    public String getData() {
        if (data == null){
            //get Data
            PreparedStatement sta;
            try {
                sta = MysqlConnector.getConnection().prepareStatement("SELECT data FROM picturesData WHERE pictureid LIKE ?");
           
            sta.setInt(1, this.id);
            ResultSet result = sta.executeQuery();
            result.next();
            this.data = result.getString("data");
             } catch (SQLException ex) {
                Logger.getLogger(Picture.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return data;
    }
    
     public static String getDataForId(int id) throws PictureLoadException {
            //get Data
            PreparedStatement sta;
            try {
                sta = MysqlConnector.getConnection().prepareStatement("SELECT data FROM picturesData WHERE pictureid LIKE ?");
           
            sta.setInt(1, id);
            ResultSet result = sta.executeQuery();
            result.next();
            return result.getString("data");
             } catch (SQLException ex) {
                Log.logWarning("couldnt load pictureData for Id " + id, Picture.class);
                throw new PictureLoadException();
            }
    }

    /**
     * @return the longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * @return the latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * @return the timestamp
     */
    public Date getTimestamp() {
        return timestamp;
    }

    /**
     * @return the upvotes
     */
    public int getUpvotes() {
        return upvotes;
    }

    /**
     * @return the downvotes
     */
    public int getDownvotes() {
        return downvotes;
    }
    /**
     * @return the userId
     */
    public int getUserId() {
        return userId;
    }

    /**
     * @return the ranking
     */
    public int getRanking() {
        return ranking;
    }

    /**
     * @param ranking the ranking to set
     */
    public void setRanking(int ranking) {
        this.ranking = ranking;
    }


}

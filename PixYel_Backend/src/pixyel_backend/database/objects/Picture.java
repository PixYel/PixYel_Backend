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
import pixyel_backend.database.exceptions.PictureLoadException;
import pixyel_backend.database.exceptions.PictureUploadExcpetion;

/**
 * @author Da_Groove
 */
public class Picture {

    private final int id;
    private final String data;
    private final double longitude;
    private final double latitude;
    private final Date timestamp;
    private final int upvotes;
    private final int downvotes;
    private final int flags;
    private final List<String> flaggedBy;
    private final int userId;
    private int rating;

    public Picture(int pictureId) throws PictureLoadException {
        this.id = pictureId;
        try {
            //get Data
            PreparedStatement sta = MysqlConnector.getConnection().prepareStatement("SELECT data FROM picturesData WHERE pictureid LIKE ?");
            sta.setInt(1, this.id);
            ResultSet result = sta.executeQuery();
            //result.next();
            this.data ="asd";// result.getString("data");

            //get Info
            sta = MysqlConnector.getConnection().prepareStatement("SELECT * FROM picturesInfo WHERE id LIKE ?");
            sta.setInt(1, this.id);
            result = sta.executeQuery();
            result.next();
            this.longitude = result.getDouble("longitude");
            this.latitude = result.getDouble("latitude");
            this.timestamp = result.getDate("upload_date");
            this.upvotes = result.getInt("upvotes");
            this.downvotes = result.getInt("downvotes");
            this.userId = result.getInt("userid");

            //get flags
            if (result.getString("flags") != null) {
                String flaggedByAsString = result.getString("flags");
                this.flaggedBy = Arrays.asList(flaggedByAsString);
                this.flags = this.flaggedBy.size();
            } else {
                this.flaggedBy = new ArrayList();
                this.flags = 0;
            }
        } catch (Exception ex) {
            Log.logWarning("couldnt load picture for Id " + pictureId + "- rootcause:" + ex, Picture.class);
            throw new PictureLoadException();
        }
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
                statement = con.prepareStatement("INSERT INTO picturesData (picutreid, data) VALUES(?,?)");
                statement.setInt(1, pictureId);
                statement.setString(2, pictureData);
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
        return data;
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
     * @return the flags
     */
    public int getFlags() {
        return flags;
    }

    /**
     * @return the flaggedBy
     */
    public List<String> getFlaggedBy() {
        return flaggedBy;
    }

    /**
     * @return the userId
     */
    public int getUserId() {
        return userId;
    }

    /**
     * @return the rating
     */
    public int getRating() {
        return rating;
    }

    /**
     * @param rating the rating to set
     */
    public void setRating(int rating) {
        this.rating = rating;
    }

    int getID() {
        return this.id;
    }
}

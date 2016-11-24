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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import pixyel_backend.Log;
import pixyel_backend.database.Columns;
import pixyel_backend.database.MysqlConnector;
import pixyel_backend.database.SqlUtils;
import pixyel_backend.database.exceptions.FlagFailedExcpetion;
import pixyel_backend.database.exceptions.PictureLoadException;
import pixyel_backend.database.exceptions.PictureUploadExcpetion;
import pixyel_backend.database.exceptions.VoteFailedException;

/**
 * @author Da_Groove & Yannick
 */
public class Picture {

    private final int id;
    private String data;
    private int ranking;
    private final Coordinate coordinate;
    private final Date timestamp;
    private final int upvotes;
    private final int downvotes;
    private final int userId;
    private final int voteStatus;

    /**
     * Create an picture-object by reading out all information of the picture
     * out of the database based on the given id
     *
     * @param pictureId
     * @param userId
     * @throws PictureLoadException if there is no picture in the database for
     * the given id
     */
    public Picture(int pictureId, int userId) throws PictureLoadException {
        this.id = pictureId;
        try {
            //get Info
            PreparedStatement sta = MysqlConnector.getConnection().prepareStatement("SELECT * FROM picturesInfo WHERE " + Columns.ID + " LIKE ?");
            sta.setInt(1, this.id);
            ResultSet result = sta.executeQuery();
            result.next();
            double longitude = result.getDouble(Columns.LONGITUDE);
            double latitude = result.getDouble(Columns.LATITUDE);
            this.coordinate = new Coordinate(longitude, latitude);
            this.timestamp = result.getDate(Columns.UPLOAD_DATE);
            this.userId = result.getInt(Columns.USER_ID);
            Log.logDebug("Loaded basic pictureInformation", sta);
            result = sta.executeQuery("SELECT COUNT(*)FROM picturesVotes WHERE " + Columns.PICTURE_ID + " = " + id + " AND " + Columns.STATUS + " = 1");
            result.next();
            this.upvotes = result.getInt(1);
            result = sta.executeQuery("SELECT COUNT(*)FROM picturesVotes WHERE " + Columns.PICTURE_ID + " = " + id + " AND " + Columns.STATUS + " = -1");
            result.next();
            this.downvotes = result.getInt(1);
            this.voteStatus = userHasLikedPicture(userId, pictureId);
        } catch (Exception ex) {
            Log.logWarning("Could not load picture for Id " + pictureId + "- rootcause:" + ex, Picture.class);
            throw new PictureLoadException();
        }

    }

    /**
     * Create an picture-object by reading out all information of the picture
     * out of the database based on the given id
     *
     * @param picId
     * @param userId
     * @return the pictureobject
     * @throws PictureLoadException if there is no picture in the database for
     * the given id
     */
    public static Picture getPictureById(int picId, int userId) throws PictureLoadException {
        return new Picture(picId, userId);
    }

    /**
     * Addes a new picture to the database
     *
     * @param userId
     * @param pictureData
     * @param longitude
     * @param latitude
     * @return
     * @throws PictureUploadExcpetion
     */
    public static synchronized Picture addNewPicture(int userId, String pictureData, double longitude, double latitude) throws PictureUploadExcpetion {
        int pictureId;
        try {
            Connection con = MysqlConnector.getConnection();
            PreparedStatement statement;
            if (pictureData != null && pictureData.length() >= 1) {
                statement = con.prepareStatement("INSERT INTO picturesInfo (" + Columns.LONGITUDE + ", " + Columns.LATITUDE + ", " + Columns.USER_ID + ") VALUES(?,?,?)");
                statement.setDouble(1, longitude);
                statement.setDouble(2, latitude);
                statement.setInt(3, userId);
                statement.executeUpdate();
                Log.logDebug("Added Picture to Db", Picture.class);
                ResultSet rs = statement.executeQuery("SELECT MAX(" + Columns.ID + ") as " + Columns.MAX_ID + " FROM picturesInfo");
                //get Id as reference for the picturedata
                rs.next();
                pictureId = rs.getInt(Columns.MAX_ID);
                pictureData = SqlUtils.escapeString(pictureData);
                statement = con.prepareStatement("INSERT INTO picturesData (" + Columns.PICTURE_ID + ", " + Columns.DATA + ") VALUES(?,?)");
                statement.setInt(1, pictureId);
                statement.setString(2, pictureData);
                statement.execute();
                statement.close();
                return new Picture(pictureId, userId);
            } else {
                Log.logInfo("Failed to add picture for user \"" + userId + "\" - rootcause: picturedata is NULL or to empty string", Picture.class);
            }
        } catch (SQLException ex) {
            Log.logError("Could not add new picture to database - rootcause: " + ex.getMessage(), Picture.class);
        } catch (PictureLoadException ex) {
            Log.logWarning(ex.getMessage(), Picture.class);
        }
        throw new PictureUploadExcpetion();
    }

    /**
     * Adds a flag
     *
     * @param userId
     * @param pictureId
     * @throws FlagFailedExcpetion
     */
    public static void flagPic(int userId, int pictureId) throws FlagFailedExcpetion {
        try (PreparedStatement statement = MysqlConnector.getConnection().prepareStatement("SELECT " + Columns.ID + " FROM  pictureflags WHERE " + Columns.PICTURE_ID + " = ? AND  " + Columns.USER_ID + " = ?")) {
            statement.setInt(1, pictureId);
            statement.setInt(2, userId);
            ResultSet result = statement.executeQuery();
            if (result == null || !result.isBeforeFirst()) {
                try (PreparedStatement instertStatement = MysqlConnector.getConnection().prepareStatement("INSERT INTO pictureflags (" + Columns.PICTURE_ID + "," + Columns.USER_ID + ") VALUES (?,?)")) {
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

    /**
     * Checks how the user has liked the picture
     *
     * @param userId
     * @param pictureId
     * @return 1 if the user has upvoted the picture, -1 if the user has
     * downvoted the picture, 0 if the user hasnt voted for the picture
     */
    public static int userHasLikedPicture(int userId, int pictureId) {
        try (PreparedStatement statement = MysqlConnector.getConnection().prepareStatement("SELECT vote FROM  picturesVotes WHERE " + Columns.PICTURE_ID + " = ? AND  " + Columns.USER_ID + " = ?")) {
            statement.setInt(1, pictureId);
            statement.setInt(2, userId);
            ResultSet result = statement.executeQuery();
            if (result == null || !result.isBeforeFirst()) {
                return 0;
            } else {
                result.next();
                return result.getInt(Columns.STATUS);
            }

        } catch (SQLException ex) {
            Log.logWarning(ex.getMessage(), Picture.class);
        }
        return 0;
    }

    /**
     * @param picId
     * @param userId
     * @throws VoteFailedException
     */
    public static void upvotePicture(int picId, int userId) throws VoteFailedException {
        changeVote(picId, userId, 1);
    }

    /**
     *
     * @param picId
     * @param userId
     * @throws VoteFailedException
     */
    public static void downvotePicture(int picId, int userId) throws VoteFailedException {
        changeVote(picId, userId, -1);
    }

    /**
     *
     * @param picId
     * @param userId
     * @throws VoteFailedException
     */
    public static void removeVoteFromPicture(int picId, int userId) throws VoteFailedException {
        changeVote(picId, userId, 0);
    }

    /**
     * Chances the vote status in the db to a new value
     *
     * @param pictureId
     * @param userId
     * @param newVoteStatus
     */
    private static void changeVote(int pictureId, int userId, int newVoteStatus) throws VoteFailedException {
        try (PreparedStatement sta = MysqlConnector.getConnection().prepareStatement("INSERT INTO picturesVotes (" + Columns.PICTURE_ID + ", " + Columns.USER_ID + ", " + Columns.STATUS + ") VALUES(?,?,?) ON DUPLICATE KEY UPDATE " + Columns.PICTURE_ID + "=?, " + Columns.USER_ID + "=?, " + Columns.STATUS + " =?")) {
            sta.setInt(1, pictureId);
            sta.setInt(4, pictureId);
            sta.setInt(2, userId);
            sta.setInt(5, userId);
            sta.setInt(3, newVoteStatus);
            sta.setInt(6, newVoteStatus);
            sta.executeUpdate();
        } catch (SQLException ex) {
            Log.logWarning(ex.getMessage(), Picture.class);
            throw new VoteFailedException();
        }
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the picturedata and writes it to the pictureobject if it isnt
     * already stored there
     *
     * @return the data
     */
    public String getData() {
        if (data == null) {
            //get Data
            PreparedStatement sta;
            try {
                sta = MysqlConnector.getConnection().prepareStatement("SELECT " + Columns.DATA + " FROM picturesData WHERE " + Columns.PICTURE_ID + " LIKE ?");

                sta.setInt(1, this.id);
                ResultSet result = sta.executeQuery();
                result.next();
                this.data = result.getString(Columns.DATA);
            } catch (SQLException ex) {
                Logger.getLogger(Picture.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return data;
    }

    /**
     * Returns the data of a picture by reading it out of the database
     *
     * @param id
     * @return
     * @throws PictureLoadException
     */
    public static String getDataForId(int id) throws PictureLoadException {
        //get Data
        PreparedStatement sta;
        try {
            sta = MysqlConnector.getConnection().prepareStatement("SELECT " + Columns.DATA + " FROM picturesData WHERE " + Columns.PICTURE_ID + " LIKE ?");

            sta.setInt(1, id);
            ResultSet result = sta.executeQuery();
            result.next();
            return result.getString(Columns.DATA);
        } catch (SQLException ex) {
            Log.logWarning("Could not load pictureData for Id" + id + " - rootcause: " + ex, Picture.class
            );
            throw new PictureLoadException();
        }
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

    /**
     * @return the coordinate
     */
    public Coordinate getCoordinate() {
        return coordinate;
    }

    /**
     * @return the voteStatus
     */
    public int getVoteStatus() {
        return voteStatus;
    }

    /**
     *
     * @param listAsString , separated list of all id example: 1,2,4,6
     * @return A map which contains all requested pictureData with their Id as
     * keys
     */
    public static Map<Integer, String> getPicturesData(String... listAsString) {
        HashMap<Integer, String> pictureList = new HashMap<>();
        for (String currentPictureIdAsString : listAsString) {
            Integer currentPictureId = Integer.valueOf(currentPictureIdAsString);
            String currentPictureData;
            try {
                currentPictureData = Picture.getDataForId(currentPictureId);
            } catch (PictureLoadException ex) {
                currentPictureData = null;
            }
            pictureList.put(currentPictureId, currentPictureData);
        }
        return pictureList;
    }
}

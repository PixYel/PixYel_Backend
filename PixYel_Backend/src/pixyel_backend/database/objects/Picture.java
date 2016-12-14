/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend.database.objects;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import pixyel_backend.Log;
import pixyel_backend.database.Columns;
import pixyel_backend.database.MysqlConnector;
import pixyel_backend.database.SqlUtils;
import pixyel_backend.database.dataProcessing.RankingCalculation;
import pixyel_backend.database.exceptions.FlagFailedExcpetion;
import pixyel_backend.database.exceptions.PictureLoadException;
import pixyel_backend.database.exceptions.PictureUploadExcpetion;
import pixyel_backend.database.exceptions.VoteFailedException;
import static pixyel_backend.database.objects.Comment.deleteComment;
import static pixyel_backend.database.objects.Comment.getCommentsForPicutre;

/**
 * @author Da_Groove & Yannick
 */
public class Picture {
    
    private final int id;
    private String data;
    private int ranking;
    private final Coordinate coordinate;
    private final Date uploadDate;
    private final Date uploadTime;
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
        try (PreparedStatement sta = MysqlConnector.getConnection().prepareStatement("SELECT * FROM picturesInfo WHERE " + Columns.ID + " LIKE ?")) {
            sta.setInt(1, this.id);
            ResultSet result = sta.executeQuery();
            result.next();
            double longitude = result.getDouble(Columns.LONGITUDE);
            double latitude = result.getDouble(Columns.LATITUDE);
            this.coordinate = new Coordinate(longitude, latitude);
            this.uploadDate = result.getDate(Columns.UPLOAD_DATE);
            this.uploadTime = result.getTime(Columns.UPLOAD_DATE);
            this.userId = result.getInt(Columns.USER_ID);
            Log.logDebug("Loaded basic pictureInformation", Picture.class);
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
     * @param result
     * @param userId
     * @throws PictureLoadException if there is no picture in the database for
     * the given id
     */
    public Picture(ResultSet result, int userId) throws PictureLoadException {
        try {
            this.id = result.getInt(Columns.ID);
            double longitude = result.getDouble(Columns.LONGITUDE);
            double latitude = result.getDouble(Columns.LATITUDE);
            this.coordinate = new Coordinate(longitude, latitude);
            this.uploadDate = result.getDate(Columns.UPLOAD_DATE);
            this.uploadTime = result.getTime(Columns.UPLOAD_DATE);
            this.userId = result.getInt(Columns.USER_ID);
            Log.logDebug("Loaded basic pictureInformation", Picture.class);
            Statement sta = MysqlConnector.getConnection().createStatement();
            result = sta.executeQuery("SELECT COUNT(*)FROM picturesVotes WHERE " + Columns.PICTURE_ID + " = " + id + " AND " + Columns.STATUS + " = 1");
            result.next();
            this.upvotes = result.getInt(1);
            result = sta.executeQuery("SELECT COUNT(*)FROM picturesVotes WHERE " + Columns.PICTURE_ID + " = " + id + " AND " + Columns.STATUS + " = -1");
            result.next();
            this.downvotes = result.getInt(1);
            this.voteStatus = userHasLikedPicture(userId, this.id);
        } catch (SQLException ex) {
            Log.logWarning("Could not load picture for ResultSet - rootcause:" + ex, Picture.class);
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
     * @param coordinate
     * @return
     * @throws PictureUploadExcpetion
     */
    public static int addNewPicture(int userId, String pictureData, Coordinate coordinate) throws PictureUploadExcpetion {
        try {
            if (pictureData != null && pictureData.length() >= 1) {
                int pictureId = addPictureInfoToDatabase(userId, coordinate);
                pictureData = SqlUtils.escapeString(pictureData);
                try (PreparedStatement statement = MysqlConnector.getConnection().prepareStatement("INSERT INTO picturesData (" + Columns.PICTURE_ID + ", " + Columns.DATA + ") VALUES(?,?)")) {
                    statement.setInt(1, pictureId);
                    statement.setString(2, pictureData);
                    statement.execute();
                    return pictureId;
                }
            } else {
                Log.logInfo("Failed to add picture for user \"" + userId + "\" - rootcause: picturedata is NULL or to empty string", Picture.class);
            }
        } catch (SQLException ex) {
            Log.logError("Could not add new picture to database - rootcause: " + ex.getMessage(), Picture.class);
        }
        throw new PictureUploadExcpetion();
    }

    /**
     *
     * @param userId
     * @param coordinate
     * @return id of the picture that was added
     * @throws SQLException
     */
    private static synchronized int addPictureInfoToDatabase(int userId, Coordinate coordinate) throws SQLException {
        try (PreparedStatement statement = MysqlConnector.getConnection().prepareStatement("INSERT INTO picturesInfo (" + Columns.LONGITUDE + ", " + Columns.LATITUDE + ", " + Columns.USER_ID + ") VALUES(?,?,?)")) {
            statement.setDouble(1, coordinate.getLongitude());
            statement.setDouble(2, coordinate.getLatitude());
            statement.setInt(3, userId);
            statement.executeUpdate();
        }
        try (PreparedStatement statement = MysqlConnector.getConnection().prepareStatement("SELECT MAX(" + Columns.ID + ") AS " + Columns.MAX_ID + " FROM picturesInfo")) {
            ResultSet rs = statement.executeQuery();
            Log.logDebug("Added Picture to database", Picture.class);
            rs.next();
            return rs.getInt(Columns.MAX_ID);
        }
    }

    /**
     * Adds a flag
     *
     * @param userId
     * @param pictureId
     * @throws FlagFailedExcpetion
     */
    public static void flagPic(int userId, int pictureId) throws FlagFailedExcpetion {
        try (PreparedStatement insertStatement = MysqlConnector.getConnection().prepareStatement("INSERT IGNORE INTO pictureflags (" + Columns.PICTURE_ID + "," + Columns.USER_ID + ") VALUES (?,?)")) {
            insertStatement.setInt(1, pictureId);
            insertStatement.setInt(2, userId);
            insertStatement.executeUpdate();
        } catch (SQLException ex) {
            Log.logWarning("Failed to flag picture " + pictureId + " rootcause: - " + ex, Comment.class);
            throw new FlagFailedExcpetion();
        }
    }

    /**
     * Checks if the user has liked the picture
     *
     * @param userId
     * @param pictureId
     * @return 1 if the user has upvoted the picture, -1 if the user has
     * downvoted the picture, 0 if the user has not voted for the picture
     */
    public static int userHasLikedPicture(int userId, int pictureId) {
        try (PreparedStatement statement = MysqlConnector.getConnection().prepareStatement("SELECT " + Columns.STATUS + " FROM  picturesVotes WHERE " + Columns.PICTURE_ID + " = ? AND  " + Columns.USER_ID + " = ?")) {
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
            Log.logError(ex.getMessage(), Picture.class);
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
            Log.logError(ex.getMessage(), Picture.class);
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
            try (PreparedStatement sta = MysqlConnector.getConnection().prepareStatement("SELECT " + Columns.DATA + " FROM picturesData WHERE " + Columns.PICTURE_ID + " LIKE ?")) {
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
        try (PreparedStatement sta = MysqlConnector.getConnection().prepareStatement("SELECT " + Columns.DATA + " FROM picturesData WHERE " + Columns.PICTURE_ID + " LIKE ?")) {
            sta.setInt(1, id);
            ResultSet result = sta.executeQuery();
            result.next();
            return result.getString(Columns.DATA);
        } catch (SQLException ex) {
            Log.logWarning("Could not load pictureData for Id" + id + " - rootcause: " + ex, Picture.class);
            throw new PictureLoadException();
        }
    }

    /**
     * @return the uploadDate
     */
    public Date getUploadDate() {
        return uploadDate;
    }

    /**
     * @return the uploadTime
     */
    public Date getUploadTime() {
        return uploadTime;
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
                pictureList.put(currentPictureId, Picture.getDataForId(currentPictureId));
            } catch (PictureLoadException ex) {
                Log.logError(ex.getMessage(), Picture.class);
                pictureList.put(currentPictureId, null);
            }
        }
        return pictureList;
    }

    /**
     * Get all Picture that are inside of a given distance to the current
     * location
     *
     * @param coordinate
     * @param searchDistance in km
     * @param userId
     * @return
     */
    public static List<Picture> getPictureByLocation(Coordinate coordinate, int searchDistance, int userId) {
        List<Picture> pictureList = new LinkedList();
        List<Coordinate> searchArea = coordinate.getSearchArea(searchDistance);
        try (PreparedStatement sta = MysqlConnector.getConnection().prepareStatement("SELECT id FROM picturesInfo WHERE (" + Columns.LONGITUDE + " BETWEEN ? AND ?) AND (" + Columns.LATITUDE + " BETWEEN ? AND ?)")) {
            sta.setDouble(1, searchArea.get(0).getLongitude());
            sta.setDouble(2, searchArea.get(1).getLongitude());
            sta.setDouble(3, searchArea.get(0).getLatitude());
            sta.setDouble(4, searchArea.get(1).getLatitude());
            ResultSet result = sta.executeQuery();
            
            if (result == null || !result.isBeforeFirst()) {
                return pictureList;
            } else {
                while (result.next()) {
                    Picture pic = Picture.getPictureById(result.getInt("id"), userId);
                    pic.setRanking(RankingCalculation.calculateRanking(pic, coordinate));
                    pictureList.add(pic);
                }
            }
        } catch (SQLException ex) {
            Log.logError(ex.toString(), User.class);
            return pictureList;
        } catch (PictureLoadException ex) {
            Log.logWarning(ex.getMessage(), User.class);
        }
        pictureList.sort((Picture pic1, Picture pic2) -> Integer.compare(pic2.getRanking(), pic1.getRanking()));
        if (pictureList.size() > 100) {
            return pictureList.subList(0, 99);
        } else {
            return pictureList;
        }
    }

    /**
     * Get the worldwide top pictures
     *
     * @param userId
     * @return
     */
    public static List<Picture> getWorldwideTopPictures(int userId) {
        List<Picture> pictureList = new LinkedList();
        try (PreparedStatement sta = MysqlConnector.getConnection().prepareStatement("SELECT id FROM picturesInfo")) {
            ResultSet result = sta.executeQuery();
            
            if (result == null || !result.isBeforeFirst()) {
                return pictureList;
            } else {
                while (result.next()) {
                    Picture pic = Picture.getPictureById(result.getInt("id"), userId);
                    pic.setRanking(RankingCalculation.calculateWorldwideRanking(pic));
                    pictureList.add(pic);
                }
            }
        } catch (SQLException ex) {
            Log.logError(ex.toString(), User.class);
            return pictureList;
        } catch (PictureLoadException ex) {
            Log.logWarning(ex.getMessage(), User.class);
        }
        pictureList.sort((Picture pic1, Picture pic2) -> Integer.compare(pic2.getRanking(), pic1.getRanking()));
        if (pictureList.size() > 100) {
            return pictureList.subList(0, 99);
        } else {
            return pictureList;
        }
    }

    /**
     * Returns a List which contains the newest pictures
     *
     * @param userId
     * @param numberofPictures max 100
     * @return
     */
    public static List<Picture> newestPictures(int numberofPictures, int userId) {
        if (numberofPictures > 100) {
            numberofPictures = 100;
        } else if (numberofPictures < 0) {
            numberofPictures = 0;
        }
        List<Picture> pictureList = new LinkedList();
        try (PreparedStatement sta = MysqlConnector.getConnection().prepareStatement("SELECT " + Columns.ID + " FROM picturesInfo ORDER BY " + Columns.ID + " DESC LIMIT ?")) {
            sta.setInt(1, numberofPictures);
            ResultSet result = sta.executeQuery();
            
            if (result == null || !result.isBeforeFirst()) {
                return pictureList;
            } else {
                while (result.next()) {
                    Picture pic = Picture.getPictureById(result.getInt("id"), userId);
                    pictureList.add(pic);
                }
            }
        } catch (SQLException ex) {
            Log.logError(ex.toString(), User.class);
            return pictureList;
        } catch (PictureLoadException ex) {
            Log.logWarning(ex.getMessage(), User.class);
        }
        return pictureList;
    }

    /**
     * Deletes the picture selected by Id and all related entrys
     * (Votes,Flags,comments,data,...)
     *
     * @param id
     */
    public static void deletePicture(int id) {
        try (PreparedStatement sta = MysqlConnector.getConnection().prepareStatement("DELETE FROM PicturesInfo WHERE " + Columns.ID + " = ?")) {
            sta.setInt(1, id);
            sta.executeUpdate();
        } catch (Exception ex) {
            Log.logWarning("Could not delete picturesInfo " + id + " - rootcause: " + ex, Comment.class);
        }
        try (PreparedStatement sta = MysqlConnector.getConnection().prepareStatement("DELETE FROM picturesVotes WHERE " + Columns.PICTURE_ID + " = ?")) {
            sta.setInt(1, id);
            sta.executeUpdate();
        } catch (Exception ex) {
            Log.logWarning("Could not delete from picturesVotes " + id + " - rootcause: " + ex, Comment.class);
        }
        
        try (PreparedStatement sta = MysqlConnector.getConnection().prepareStatement("DELETE FROM pictureflags WHERE " + Columns.PICTURE_ID + " = ?")) {
            sta.setInt(1, id);
            sta.executeUpdate();
        } catch (Exception ex) {
            Log.logWarning("Could not delete from pictureflags " + id + " - rootcause: " + ex, Comment.class);
        }
        
        try (PreparedStatement sta = MysqlConnector.getConnection().prepareStatement("DELETE FROM picturesData WHERE " + Columns.PICTURE_ID + " = ?")) {
            sta.setInt(1, id);
            sta.executeUpdate();
        } catch (Exception ex) {
            Log.logWarning("Could not delete from picturesData " + id + " - rootcause: " + ex, Comment.class);
        }
        
        LinkedList<Comment> commentList = new LinkedList(Comment.getCommentsForPicutre(id));
        for (Comment comment : commentList) {
            try {
                Comment.deleteComment(comment.getCommentId());
            } catch (Exception ex) {
                Log.logWarning("Could not delete from Comments " + id + " - rootcause: " + ex, Comment.class);
            }
        }
    }

    public static List<Picture> getPictureByUser(int userId, int forUser) {
        List<Picture> allPictures = new LinkedList<>();
        try (Statement sta = MysqlConnector.getConnection().createStatement()) {
            ResultSet result = sta.executeQuery("SELECT * FROM picturesInfo WHERE " + Columns.USER_ID + " = " + userId);
            if (result != null && result.isBeforeFirst()) {
                while (result.next()) {
                    try {
                        allPictures.add(new Picture(result, forUser));
                    } catch (PictureLoadException ex) {
                        Log.logWarning(ex.getMessage(), User.class);
                    }
                }
            }
            return allPictures;
        } catch (SQLException ex) {
            Log.logError(ex.getMessage(), User.class);
            return allPictures;
        }
    }
    
    public int countFlags(){
        try(Statement sta = MysqlConnector.getConnection().createStatement()){
            ResultSet result = sta.executeQuery("SELECT COUNT(*)FROM pictureflags WHERE " + Columns.PICTURE_ID + " = " + id);
            if (result == null || !result.isBeforeFirst()) {
                return 0;
            } else {
                result.next();
                return result.getInt(1);
            }
        }catch(SQLException ex){
            Log.logError(ex.getMessage(), Picture.class);
            return 0;
        }
    }
    
    
}

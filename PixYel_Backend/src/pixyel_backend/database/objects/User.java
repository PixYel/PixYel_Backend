package pixyel_backend.database.objects;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import pixyel_backend.Log;
import pixyel_backend.database.BackendFunctions;
import pixyel_backend.database.MysqlConnector;
import pixyel_backend.database.exceptions.UserCreationException;
import pixyel_backend.database.exceptions.UserNotFoundException;
import pixyel_backend.database.SqlUtils;
import pixyel_backend.database.dataProcessing.RankingCalculation;
import pixyel_backend.database.exceptions.FlagFailedExcpetion;
import pixyel_backend.database.exceptions.NoPicturesFoundExcpetion;
import pixyel_backend.database.exceptions.PictureLoadException;
import pixyel_backend.database.exceptions.PictureUploadExcpetion;
import pixyel_backend.database.exceptions.VoteFailedException;

public class User {
    private final int id;
    private final String storeID;
    private String publicKey;
    private boolean isBanned;
    private final Timestamp registrationDate;

    /**
     * creates a user which by reading out all information about the user from
     * the db
     *
     * @param id
     * @throws UserNotFoundException
     * @throws pixyel_backend.database.exceptions.UserCreationException
     *
     */
    public User(int id) throws UserNotFoundException, UserCreationException {

        try {
            PreparedStatement sta = MysqlConnector.getConnection().prepareStatement("SELECT * FROM users WHERE id LIKE ?");
            sta.setInt(1, id);
            ResultSet result = sta.executeQuery();
            if (result == null || !result.isBeforeFirst()) {
                throw new UserNotFoundException();
            }
            result.next();
            this.id = id;
            this.storeID = result.getString("storeid");
            this.publicKey = result.getString("publickey");
            this.registrationDate = result.getTimestamp("reg_date");

            int status = result.getInt("status");
            if (status < 0) {
                this.isBanned = true;
            } else {
                this.isBanned = false;
            }
        } catch (SQLException ex) {
            Log.logError("Could not read userinformation from database - rootcause: " + ex.getMessage(), User.class);
            throw new UserCreationException();
        }
    }

    /**
     * creates a user which by reading out all information about the user from
     * the db
     *
     * @param storeID
     * @throws UserNotFoundException
     * @throws pixyel_backend.database.exceptions.UserCreationException
     */
    public User(String storeID) throws UserNotFoundException, UserCreationException {
        try {
            PreparedStatement sta = MysqlConnector.getConnection().prepareStatement("SELECT * FROM users WHERE storeid LIKE ?");
            sta.setString(1, SqlUtils.escapeString(storeID));
            ResultSet result = sta.executeQuery();

            if (result == null || !result.isBeforeFirst()) {
                throw new UserNotFoundException();
            }
            result.next();
            this.id = result.getInt("id");
            this.storeID = result.getString("storeid");
            this.publicKey = result.getString("publickey");
            this.registrationDate = result.getTimestamp("reg_date");

            int status = result.getInt("status");
            if (status < 0) {
                this.isBanned = true;
            } else {
                this.isBanned = false;
            }
        } catch (SQLException ex) {
            Log.logError("Could not read userinformation from database - rootcause: " + ex.getMessage(), User.class);
            throw new UserCreationException();
        }
    }

    /**
     * static methode to get a User
     *
     * @param id
     * @return
     * @throws UserCreationException
     */
    public static User getUser(int id) throws UserNotFoundException, UserCreationException {
        return new User(id);
    }

    /**
     * static methode to get a User
     *
     * @param storeID
     * @return
     * @throws UserCreationException
     */
    public static User getUser(String storeID) throws UserNotFoundException, UserCreationException {
        return new User(storeID);
    }

    /**
     * Adds a user to the productiv Database
     *
     * @param storeID storeId of the client should not be null
     * @return the User that was created
     * @throws pixyel_backend.database.exceptions.UserCreationException if
     * creation fails
     */
    public static User addNewUser(String storeID) throws UserCreationException {
        addUserToDb(storeID);
        return new User(storeID);
    }

    /**
     * Adds a user to a Database
     */
    private static void addUserToDb(String storeID) throws UserCreationException {
        try (PreparedStatement statement = MysqlConnector.getConnection().prepareStatement("INSERT INTO users(storeid)VALUES (?)")) {
            storeID = SqlUtils.escapeString(storeID);
            statement.setString(1, storeID);
            statement.executeUpdate();

        } catch (SQLException ex) {
            Log.logWarning("Could not create user for storeid \"" + storeID + "\" - rootcause: " + ex, User.class
            );
            throw new UserCreationException();
        }
    }

    public int getID() {
        return this.id;
    }

    public boolean isBanned() {
        return this.isBanned;
    }

    public void setBanned(boolean setBanned) {
        this.isBanned = setBanned;
        if (setBanned) {
            changeStatusTo(-1);
        } else {
            changeStatusTo(1);
        }
    }

    public String getPublicKey() {
        return this.publicKey;
    }

    public void setPublicKey(String key) {
        this.publicKey = key;
        updateUserValue("publickey", key);
    }

    public String getStoreID() {
        return storeID;
    }

    private void changeStatusTo(int i) {
        if (i < 0) {
            this.isBanned = true;
        } else {
            this.isBanned = false;
        }
        updateUserValue("status", i);
    }

    /**
     * Updates a user atribute in the database in the database
     *
     * @param key name auf the table collum
     * @param value value that should be inserted
     */
    private void updateUserValue(String key, String value) {
        try (PreparedStatement sta = MysqlConnector.getConnection().prepareStatement("UPDATE users SET " + key + " = ? WHERE id LIKE " + this.id)) {
            sta.setString(1, SqlUtils.escapeString(value));
            sta.execute();
        } catch (SQLException ex) {
            Log.logError("couldnt update user value \"" + key + "\" - rootcause:" + ex.getMessage(), this);
        }
    }

    /**
     * Updates a user atribute in the database in the database
     *
     * @param key name auf the table collum
     * @param value value that should be inserted
     */
    private void updateUserValue(String key, int value) {
        try (PreparedStatement sta = MysqlConnector.getConnection().prepareStatement("UPDATE users SET " + key + " = ? WHERE id LIKE " + this.id)) {
            sta.setInt(1, value);
            sta.execute();
        } catch (SQLException ex) {
            Log.logError("couldnt update user value \"" + key + "\" - rootcause:" + ex.getMessage(), this);
        }

    }

    public Timestamp getRegistrationDate() {
        return registrationDate;
    }

    /**
     * deletes this user from the database
     */
    public void delete() {
        try (PreparedStatement sta = MysqlConnector.getConnection().prepareStatement("DELETE FROM Users WHERE id = ?")) {
            sta.setInt(1, this.id);
            sta.executeUpdate();

        } catch (Exception e) {
            Log.logWarning("Couldnt delete user \"" + this.id + "\" - rootcause:" + e, this);
        }
    }

    /**
     * @param commentId
     * @throws pixyel_backend.database.exceptions.FlagFailedExcpetion
     * @see pixyel_backend.database.objects.Comment.addFlag
     */
    public synchronized void flagComment(int commentId) throws FlagFailedExcpetion {
        Comment.addFlag(this.id, commentId);
    }
    
    
    /**
     * @param text
     * @param refersToPicuture
     * @see pixyel_backend.database.objects.Comment
     */
    public void addNewComment(String text, int refersToPicuture) {
        Comment.newComment(refersToPicuture, id, text);
    }

    /**
     * @param data
     * @param longitude
     * @param latitude
     * @return The pictureobject of the picture that was uploaded
     * @throws pixyel_backend.database.exceptions.PictureUploadExcpetion
     * @throws pixyel_backend.database.exceptions.PictureLoadException
     * @see pixyel_backend.database.objects.Picture
     */
    public Picture uploadPicture(String data, double longitude, double latitude) throws PictureUploadExcpetion, PictureLoadException {
        return Picture.addNewPicture(id, data, longitude, latitude);
    }
    
    public synchronized void flagPicture(int pictureId) throws FlagFailedExcpetion{
        Picture.flagPic(id, pictureId);
    }
    public List<Picture> getPicturesList(Coordinate cord, int searchDistance) throws NoPicturesFoundExcpetion {
        List<Picture> pictureList = new LinkedList();
        List<Coordinate> searchArea = cord.getSearchArea(searchDistance);
        try (PreparedStatement sta = MysqlConnector.getConnection().prepareStatement("SELECT id FROM picturesInfo WHERE (longitude BETWEEN ? AND ?) AND (latitude BETWEEN ? AND ?)")) {
            sta.setDouble(1, searchArea.get(0).getLongitude());
            sta.setDouble(2, searchArea.get(1).getLongitude());
            sta.setDouble(3, searchArea.get(0).getLatitude());
            sta.setDouble(4, searchArea.get(1).getLatitude());
            ResultSet result = sta.executeQuery();

            if (result == null || !result.isBeforeFirst()) {
                throw new NoPicturesFoundExcpetion();
            } else {
                while (result.next()) {
                    Picture pic = Picture.getPictureById(result.getInt("id"),this.id);
                    pic.setRanking(RankingCalculation.calculateRanking(pic, cord));
                    pictureList.add(pic);
                }
            }
        } catch (SQLException ex) {
            Log.logWarning(ex.toString(), BackendFunctions.class);
            throw new NoPicturesFoundExcpetion();
        } catch (PictureLoadException ex) {
            Log.logWarning(ex.getMessage(), BackendFunctions.class);
        }
        pictureList.sort((Picture pic1, Picture pic2) -> Integer.compare(pic1.getRanking(), pic2.getRanking()));
        if (pictureList.size() > 100) {
            return pictureList.subList(0, 99);
        } else {
            return pictureList;
        }
    }
    
    /**
     * returns a list of max. 100 pictures by using the top ranked pictures
     * inside a searchradius of 20km (headinformation only)
     *
     * @param cord current location of the user who requests the Photos
     * @return
     * @throws pixyel_backend.database.exceptions.NoPicturesFoundExcpetion
     */
    public List<Picture> getPicturesList(Coordinate cord) throws NoPicturesFoundExcpetion {
        return getPicturesList(cord, 20);
    }
    
        /**
     *
     * @param listAsString , separated list of all id example: 1,2,4,6
     * @return A map which contains all requested pictures with their Id as
     * keys
     */
    public Map<Integer, Picture> getPicturesStats(String listAsString) {
        HashMap<Integer, Picture> pictureList = new HashMap<>();
        List<String> allRequestedPictures = Arrays.asList(listAsString);
        for (String currentPictureIdAsString : allRequestedPictures) {
            Integer currentPictureId = Integer.valueOf(currentPictureIdAsString);
            Picture currentPicture;
            try {
                currentPicture = Picture.getPictureById(currentPictureId, this.id);
            } catch (PictureLoadException ex) {
                currentPicture = null;
            }
            pictureList.put(currentPictureId, currentPicture);
        }
        return pictureList;
    }
    
    public synchronized void upvotePicture(int picId) throws VoteFailedException {
        Picture.upvotePicture(picId, this.id);
    }

    public synchronized void downvotePicture(int picId) throws VoteFailedException {
        Picture.downvotePicture(picId, this.id);
    }
    
     /**
     * Create an picture-object by reading out all information of the picture
     * out of the database based on the given id
     *
     * @param picId
     * @return the pictureobject
     * @throws PictureLoadException if there is no picture in the database for
     * the given id
     */
    public Picture getPictureById(int picId) throws PictureLoadException {
        return Picture.getPictureById(picId, this.id);
    }

}

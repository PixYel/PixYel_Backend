package pixyel_backend.database.objects;

import com.vaadin.ui.Grid;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import pixyel_backend.Log;
import pixyel_backend.database.Columns;
import pixyel_backend.database.MysqlConnector;
import pixyel_backend.database.exceptions.UserCreationException;
import pixyel_backend.database.exceptions.UserNotFoundException;
import pixyel_backend.database.SqlUtils;
import pixyel_backend.database.dataProcessing.RankingCalculation;
import pixyel_backend.database.exceptions.CommentCreationException;
import pixyel_backend.database.exceptions.FlagFailedExcpetion;
import pixyel_backend.database.exceptions.PictureLoadException;
import pixyel_backend.database.exceptions.PictureUploadExcpetion;
import pixyel_backend.database.exceptions.VoteFailedException;
import static pixyel_backend.database.objects.Picture.userHasLikedPicture;

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
            PreparedStatement sta = MysqlConnector.getConnection().prepareStatement("SELECT * FROM users WHERE " + Columns.ID + " = ?");
            sta.setInt(1, id);
            ResultSet result = sta.executeQuery();
            if (result == null || !result.isBeforeFirst()) {
                throw new UserNotFoundException();
            }
            result.next();
            this.id = id;
            this.storeID = result.getString(Columns.STORE_ID);
            this.publicKey = result.getString(Columns.PUBLICKEY);
            this.registrationDate = result.getTimestamp(Columns.REGISTRATION_DATE);

            int status = result.getInt(Columns.STATUS);
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
            PreparedStatement sta = MysqlConnector.getConnection().prepareStatement("SELECT * FROM users WHERE " + Columns.STORE_ID + " LIKE ?");
            sta.setString(1, SqlUtils.escapeString(storeID));
            ResultSet result = sta.executeQuery();

            if (result == null || !result.isBeforeFirst()) {
                throw new UserNotFoundException();
            }
            result.next();
            this.id = result.getInt(Columns.ID);
            this.storeID = result.getString(Columns.STORE_ID);
            this.publicKey = result.getString(Columns.PUBLICKEY);
            this.registrationDate = result.getTimestamp(Columns.REGISTRATION_DATE);

            int status = result.getInt(Columns.STATUS);
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
        try (PreparedStatement statement = MysqlConnector.getConnection().prepareStatement("INSERT INTO users(" + Columns.STORE_ID + ")VALUES (?)")) {
            storeID = SqlUtils.escapeString(storeID);
            statement.setString(1, storeID);
            statement.executeUpdate();

        } catch (SQLException ex) {
            Log.logWarning("Could not create user for storeid \"" + storeID + "\" - rootcause: " + ex, User.class
            );
            throw new UserCreationException();
        }
    }

    /**
     * @return the id of the user
     */
    public int getID() {
        return this.id;
    }

    /**
     * Returns if the user is banned - this information is based on the collum
     * status in the db (status 0 < 0 ->banned)
     *
     * @return
     */
    public boolean isBanned() {
        return this.isBanned;
    }

    /**
     *
     * @param setBanned true if the user should be banned. False unbannes the
     * user
     */
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

    /**
     * Write a new public key into the database for the current user
     *
     * @param key
     */
    public void setPublicKey(String key) {
        this.publicKey = key;
        updateUserValue(Columns.PUBLICKEY, key);
    }

    /**
     * Gets the storeId of the current user The storeid is a unique
     * identificationtext which is linked to the storeaccount (googleplay,
     * windowsstore or itunes)
     *
     * @return
     */
    public String getStoreID() {
        return storeID;
    }

    /**
     * Chance the usersstatus in the db
     *
     * @param i the new status that should be entered in the db
     */
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
        try (PreparedStatement sta = MysqlConnector.getConnection().prepareStatement("UPDATE users SET " + key + " = ? WHERE " + Columns.ID + " LIKE " + this.id)) {
            sta.setString(1, SqlUtils.escapeString(value));
            sta.execute();
        } catch (SQLException ex) {
            Log.logError("Could not update user value \"" + key + "\" - rootcause:" + ex.getMessage(), User.class);
        }
    }

    /**
     * Updates a user atribute in the database in the database
     *
     * @param key name auf the table collum
     * @param value value that should be inserted
     */
    private void updateUserValue(String key, int value) {
        try (PreparedStatement sta = MysqlConnector.getConnection().prepareStatement("UPDATE users SET " + key + " = ? WHERE " + Columns.ID + " LIKE " + this.id)) {
            sta.setInt(1, value);
            sta.execute();
        } catch (SQLException ex) {
            Log.logError("Could not update user value \"" + key + "\" - rootcause:" + ex.getMessage(), User.class);
        }

    }

    /**
     * @return the date when the user was added to the database
     */
    public Timestamp getRegistrationDate() {
        return registrationDate;
    }

    /**
     * deletes this user from the database
     */
    public void delete() {
        try (PreparedStatement sta = MysqlConnector.getConnection().prepareStatement("DELETE FROM Users WHERE " + Columns.ID + " = ?")) {
            sta.setInt(1, this.id);
            sta.executeUpdate();

        } catch (Exception e) {
            Log.logWarning("Could not delete user \"" + this.id + "\" - rootcause:" + e, User.class);
        }
    }

    /**
     * @param commentId
     * @throws pixyel_backend.database.exceptions.FlagFailedExcpetion
     * @see pixyel_backend.database.objects.Comment.addFlag
     */
    public synchronized void flagComment(int commentId) throws FlagFailedExcpetion {
        Comment.flagComment(this.id, commentId);
    }

    /**
     * @param text
     * @param refersToPicuture
     * @throws pixyel_backend.database.exceptions.CommentCreationException
     * @see pixyel_backend.database.objects.Comment
     */
    public void addNewComment(String text, int refersToPicuture) throws CommentCreationException {
        Comment.addComment(refersToPicuture, id, text);
    }

    /**
     * @param data
     * @param cord
     * @return The pictureobject of the picture that was uploaded
     * @throws pixyel_backend.database.exceptions.PictureUploadExcpetion
     * @see pixyel_backend.database.objects.Picture
     */
    public int uploadPicture(String data, Coordinate cord) throws PictureUploadExcpetion {
        return Picture.addNewPicture(id, data, cord);
    }

    /**
     * @param pictureId
     * @throws FlagFailedExcpetion
     */
    public synchronized void flagPicture(int pictureId) throws FlagFailedExcpetion {
        Picture.flagPic(id, pictureId);
    }

    /**
     * Get all Picture that are inside of a given distance to the current
     * location
     *
     * @param cord
     * @param searchDistance
     * @return
     */
    public List<Picture> getPicturesByLocation(Coordinate cord, int searchDistance) {
        List<Picture> pictureList = new LinkedList();
        List<Coordinate> searchArea = cord.getSearchArea(searchDistance);
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
                    Picture pic = Picture.getPictureById(result.getInt("id"), this.id);
                    pic.setRanking(RankingCalculation.calculateRanking(pic, cord));
                    pictureList.add(pic);
                }
            }
        } catch (SQLException ex) {
            Log.logError(ex.toString(), User.class);
            return pictureList;
        } catch (PictureLoadException ex) {
            Log.logWarning(ex.getMessage(), User.class);
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
     */
    public List<Picture> getPicturesByLocation(Coordinate cord) {
        return getPicturesByLocation(cord, 20);
    }

    /**
     *
     * @param listAsString , separated list of all id example: 1,2,4,6
     * @return A map which contains all requested pictures with their Id as keys
     */
    public Map<Integer, Picture> getPictures(String... listAsString) {
        HashMap<Integer, Picture> pictureList = new HashMap<>();
        for (String currentPictureIdAsString : listAsString) {
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

    /**
     *
     * @param picId
     * @throws VoteFailedException
     */
    public synchronized void upvotePicture(int picId) throws VoteFailedException {
        Picture.upvotePicture(picId, this.id);
    }

    /**
     *
     * @param picId
     * @throws VoteFailedException
     */
    public synchronized void downvotePicture(int picId) throws VoteFailedException {
        Picture.downvotePicture(picId, this.id);
    }

    /**
     *
     * @param picId
     * @throws VoteFailedException
     */
    public synchronized void removeVoteFromPicture(int picId) throws VoteFailedException {
        Picture.removeVoteFromPicture(picId, this.id);
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
    public Picture getPicture(int picId) throws PictureLoadException {
        return Picture.getPictureById(picId, this.id);
    }

    /**
     * Creates a List of all pictureIds that were liked by the user
     * @return 
     */
    public List<Integer> allLikedPictures() {
        List<Integer> allLikedPictures = new LinkedList<>();
        try (Statement sta = MysqlConnector.getConnection().createStatement()) {
            ResultSet result = sta.executeQuery("SELECT " + Columns.PICTURE_ID + " FROM picturesVotes WHERE " + Columns.USER_ID + " = " + this.id);
            if (result != null && result.isBeforeFirst()) {
                while (result.next()) {
                    int pictureId = result.getInt(1);
                    allLikedPictures.add(pictureId);
                }
            }
            return allLikedPictures;
        } catch (SQLException ex) {
            Log.logError(ex.getMessage(), User.class);
            return allLikedPictures;
        }
    }
}

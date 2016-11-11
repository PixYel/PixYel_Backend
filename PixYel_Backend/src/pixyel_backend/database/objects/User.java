package pixyel_backend.database.objects;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;
import pixyel_backend.Log;
import pixyel_backend.database.BackendFunctions;
import pixyel_backend.database.MysqlConnector;
import pixyel_backend.database.exceptions.UserCreationException;
import pixyel_backend.database.exceptions.UserNotFoundException;
import pixyel_backend.database.SqlUtils;
import pixyel_backend.database.exceptions.PictureLoadException;
import pixyel_backend.database.exceptions.PictureUploadExcpetion;

public class User {

    private static final Connection CONNECTION = MysqlConnector.getConnection();
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
            PreparedStatement sta = CONNECTION.prepareStatement("SELECT * FROM users WHERE id LIKE ?");
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
            PreparedStatement sta = CONNECTION.prepareStatement("SELECT * FROM users WHERE storeid LIKE ?");
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
        try (PreparedStatement sta = CONNECTION.prepareStatement("UPDATE users SET " + key + " = ? WHERE id LIKE " + this.id)) {
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
        try (PreparedStatement sta = CONNECTION.prepareStatement("UPDATE users SET " + key + " = ? WHERE id LIKE " + this.id)) {
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
        try (PreparedStatement sta = CONNECTION.prepareStatement("DELETE FROM Users WHERE id = ?")) {
            sta.setInt(1, this.id);
            sta.executeUpdate();

        } catch (Exception e) {
            Log.logWarning("Couldnt delete user \"" + this.id + "\" - rootcause:" + e, this);
        }
    }

    /**
     * @see pixyel_backend.database.objects.Comment.addFlag
     */
    public void flagComment(int commentId) throws SQLException {
        Comment.addFlag(this.id, commentId);
    }
    
    
    /**
     * @see pixyel_backend.database.objects.Comment
     */
    public void addNewComment(String text, int refersToPicuture) {
        Comment.newComment(refersToPicuture, id, text);
    }

    public Picture uploadPicture(String data, double longitude, double latitude) throws PictureUploadExcpetion, PictureLoadException {
        return Picture.addNewPicture(id, data, longitude, latitude);
    }
    
}

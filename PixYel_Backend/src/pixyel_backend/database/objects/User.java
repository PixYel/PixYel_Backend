package pixyel_backend.database.objects;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.logging.Logger;
import pixyel_backend.ServerLog;
import pixyel_backend.database.Exceptions.DbConnectionException;
import pixyel_backend.database.MysqlConnector;
import pixyel_backend.database.SqlUtils;

public class User {
    private final static Logger LOG = Logger.getLogger(User.class.getName());
    private final int id;
    private final String storeID;
    private String publicKey;
    private boolean isBanned;
    private boolean isVerified;
    private int amountSMSsend;
    private final Timestamp registrationDate;

    public User(int id) throws UserNotFoundException, SQLException, DbConnectionException {
        try (Connection conn = MysqlConnector.connectToDatabaseUsingPropertiesFile(); Statement sta = conn.createStatement()) {
            ResultSet result = sta.executeQuery("SELECT * FROM users WHERE id LIKE " + id);
            if (!result.isBeforeFirst()) {
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
            if (status > 0) {
                this.isVerified = true;
            } else {
                this.isVerified = false;
            }
        }
    }

    public User(String storeID) throws UserNotFoundException, DbConnectionException, SQLException {
        try (Connection conn = MysqlConnector.connectToDatabaseUsingPropertiesFile()) {
            ResultSet result = null;
            try {
                PreparedStatement sta = conn.prepareStatement("SELECT * FROM users WHERE storeid LIKE ?");
                sta.setString(1, SqlUtils.escapeString(storeID));
                result = sta.executeQuery();
            } catch (SQLException sqlEx) {
                ServerLog.logInfo(sqlEx.toString());
            }

            if (result == null || !result.isBeforeFirst()) {
                throw new UserNotFoundException();
            }
            result.next();
            this.id = result.getInt("id");
            this.storeID = result.getString("storeid");
            this.publicKey = result.getString("publickey");
            this.amountSMSsend = result.getInt("amountSMSsend");
            this.registrationDate = result.getTimestamp("reg_date");

            int status = result.getInt("status");
            if (status < 0) {
                this.isBanned = true;
            } else {
                this.isBanned = false;
            }
            if (status > 0) {
                this.isVerified = true;
            } else {
                this.isVerified = false;
            }
        }
    }

    public static User getUser(int id) throws Exception {
        try {
            return new User(id);
        } catch (UserNotFoundException | SQLException | DbConnectionException e) {
            System.out.println(e);
            return null;
        }
    }

    public static User getUser(String storeID) throws Exception {
        try {
            return new User(storeID);
        } catch (UserNotFoundException | DbConnectionException | SQLException e) {
            System.out.println(e);
            return null;
        }
    }

    /**
     * Adds a user to a Database
     *
     * @param storeID storeId of the client should not be null
     * @return the User that was created
     * @throws SQLException
     */
    public static User addNewUser(String storeID) throws Exception {
        try (Connection conn = MysqlConnector.connectToDatabaseUsingPropertiesFile()) {
            try (PreparedStatement statement = conn.prepareStatement("INSERT INTO users(storeid)VALUES (?)")) {
                storeID = SqlUtils.escapeString(storeID);
                statement.setString(1, storeID);
                statement.executeUpdate();
            }
        }
        return getUser(storeID);
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

    public boolean isVerified(int ID) {
        return isVerified();
    }

    public void setVerified(boolean verified) {
        this.isVerified = verified;
        if (verified) {
            changeStatusTo(1);
        } else {
            changeStatusTo(0);
        }
    }

    public String getPublicKey() {
        return this.publicKey;
    }

    public void setPublicKey(String key) {
        updateUserValue("publickey", key);
    }

    public int getAmountSMSsend() {
        return amountSMSsend;
    }

    public void setAmountSMSsend(int amount) {
        updateUserValue("amountSMSsend", amount);
    }

    public void raiseAmountSMSsend() {
        this.amountSMSsend += 1;
        setAmountSMSsend(this.amountSMSsend);
    }

    /**
     * @return the deviceID
     */
    public String getStoreID() {
        return storeID;
    }

    public boolean isVerified() {
        return isVerified;
    }

    private void changeStatusTo(int i) {
        updateUserValue("status", i);
    }

    private void updateUserValue(String key, String value) {
        try {
            try (Connection conn = MysqlConnector.connectToDatabaseUsingPropertiesFile(); PreparedStatement sta = conn.prepareStatement("UPDATE users SET " + key + " = ? WHERE id LIKE " + this.id)) {
                sta.setString(1, SqlUtils.escapeString(value));
                sta.execute();
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    private void updateUserValue(String key, int value) {
        try {
            try (Connection conn = MysqlConnector.connectToDatabaseUsingPropertiesFile(); PreparedStatement sta = conn.prepareStatement("UPDATE users SET " + key + " = ? WHERE id LIKE " + this.id)) {
                sta.setInt(1, value);
                sta.execute();
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    /**
     * @return the registrationDate
     */
    public Timestamp getRegistrationDate() {
        return registrationDate;
    }

    public void delete() {
        try {
            try (Connection conn = MysqlConnector.connectToDatabaseUsingPropertiesFile(); Statement sta = conn.createStatement()) {
                sta.executeLargeUpdate("DELETE FROM Users WHERE id = " + this.id);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}

class UserNotFoundException extends RuntimeException {
}

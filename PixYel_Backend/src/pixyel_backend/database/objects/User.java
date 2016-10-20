package pixyel_backend.database.objects;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import pixyel_backend.database.DatabaseFunctions;
import pixyel_backend.database.MysqlConnector;
import pixyel_backend.database.SqlUtils;

public class User {

    private final int id;
    private final String storeID;
    private String publicKey;
    private boolean isBanned;
    private boolean isVerified;
    private int amountSMSsend;
    private final Timestamp registrationDate;

    public User(int id) throws Exception {
        try (Connection conn = MysqlConnector.connectToDatabaseUsingPropertiesFile(); Statement sta = conn.createStatement()) {
            ResultSet result = sta.executeQuery("SELECT * FROM users WHERE id LIKE " + id);
            if (!result.isBeforeFirst()) {
                throw new Exception("no user found");
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

    public User(String storeID) throws Exception {
        try (Connection conn = MysqlConnector.connectToDatabaseUsingPropertiesFile(); Statement sta = conn.createStatement()) {
            ResultSet result = sta.executeQuery("SELECT * FROM users WHERE storeid LIKE '" + storeID + "'");
            if (!result.isBeforeFirst()) {
                throw new Exception("no user found");
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
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public static User getUser(String storeID) throws Exception {
        try {
            return new User(storeID);
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public static void addNewUser(String storeID) throws Exception {
        DatabaseFunctions func = new DatabaseFunctions();
        func.addNewUser(storeID);
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

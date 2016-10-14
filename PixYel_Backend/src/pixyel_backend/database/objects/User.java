package pixyel_backend.database.objects;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import pixyel_backend.database.DatabaseFunctions;
import pixyel_backend.database.MysqlConnector;
import pixyel_backend.database.SqlUtils;

public class User {

    private final int id;
    private int telephonenumber;
    private String deviceID;
    private String publicKey;
    private boolean isBanned;
    private boolean isVerified;
    private int amountSMSsend;

    public User(int id) throws Exception {
        Connection conn = MysqlConnector.connectToDatabaseUsingPropertiesFile();
        Statement sta = conn.createStatement();
        ResultSet result = sta.executeQuery("SELECT * FROM users WHERE userid LIKE " + id);
        if (!result.isBeforeFirst()) {
            throw new Exception("no user found");
        }
        result.next();
        this.id = id;
        this.telephonenumber = result.getInt("phonenumber");
        this.deviceID = result.getString("deviceID");
        this.publicKey = result.getString("publickey");
        this.amountSMSsend = result.getInt("amountSMSsend");

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
        sta.close();
        conn.close();
    }

    public User(int telephoneNumber, String deviceID) throws Exception {
        Connection conn = MysqlConnector.connectToDatabaseUsingPropertiesFile();
        Statement sta = conn.createStatement();
        ResultSet result = sta.executeQuery("SELECT * FROM users WHERE phonenumber LIKE " + telephoneNumber + " AND deviceID LIKE '" + deviceID + "'");
        if (!result.isBeforeFirst()) {
            throw new Exception("no user found");
        }
        result.next();
        this.id = result.getInt("userID");;
        this.telephonenumber = result.getInt("phonenumber");
        this.deviceID = result.getString("deviceID");
        this.publicKey = result.getString("publickey");
        this.amountSMSsend = result.getInt("amountSMSsend");

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
        sta.close();
        conn.close();
    }

    public static User getUser(int id) throws Exception {
        try {
            return new User(id);
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public static User getUser(int telephoneNumber, String deviceID) throws Exception {
        try {
            return new User(telephoneNumber, deviceID);
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public static void addNewUser(int telephoneNumber, String deviceID) throws Exception {
        DatabaseFunctions func = new DatabaseFunctions();
        func.addNewUser(telephoneNumber, deviceID);
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
     * @return the telephonenumber
     */
    public int getTelephonenumber() {
        return telephonenumber;
    }
    
    public void setTelephonenumber(int newNumber){
        this.telephonenumber=newNumber;
        updateUserValue("phonenumber", newNumber);
    }

    /**
     * @return the deviceID
     */
    public String getDeviceID() {
        return deviceID;
    }
    
    public void setDeviceID(String newDeviceID){
        this.deviceID=newDeviceID;
        updateUserValue("deviceID", newDeviceID);
    }

    public boolean isVerified() {
        return isVerified;
    }

    private void changeStatusTo(int i) {
        updateUserValue("status", i);
    }

    private void updateUserValue(String key, String value) {
        try {
            Connection conn = MysqlConnector.connectToDatabaseUsingPropertiesFile();
            PreparedStatement sta = conn.prepareStatement("UPDATE users SET " + key + " = ? WHERE userid LIKE " + this.id);
            sta.setString(1, SqlUtils.escapeString(value));
            sta.execute();
            sta.close();
            conn.close();
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    private void updateUserValue(String key, int value) {
        try {
            Connection conn = MysqlConnector.connectToDatabaseUsingPropertiesFile();
            PreparedStatement sta = conn.prepareStatement("UPDATE users SET " + key + " = ? WHERE userid LIKE " + this.id);
            sta.setInt(1, value);
            sta.execute();
            sta.close();
            conn.close();
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
}

package pixyel_backend.database.objects;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import pixyel_backend.database.DatabaseFunctions;
import pixyel_backend.database.MysqlConnector;

public class User {

    private final int id;
    private final int telephonenumber;
    private final String deviceID;
    private final String publicKey;
    private final boolean banned;
    private final boolean verified;
    private final int amountSMSsend;

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
        if (status < 0){
            this.banned=true;
        }
        else{
            this.banned=false;
        }
        if (status > 0){
            this.verified = true;
        }
        else{
            this.verified=false;
        }
        sta.close();
        conn.close();
    }

    public static User getUser(int id) throws Exception {
        try {
            return new User(id);
        } catch (Exception e) {
            return null;
        }
    }

    public static User getUser(int telephoneNumber, String deviceID) throws Exception {
        try {
            return new User(telephoneNumber, deviceID);
        } catch (Exception e) {
            return null;
        }
    }
    
    public static void addNewUser(String telephoneNumber, String deviceID) throws Exception{
        DatabaseFunctions func = new DatabaseFunctions();
        func.addNewUser(telephoneNumber, deviceID);
    }

    public User(int telephoneNumber, String deviceID) throws Exception {
        try {
            //get user
        } catch (Exception e) {
            throw new Exception("user not found");
        }
        this.id = 0;
        this.telephonenumber = 0;
        this.deviceID = null;
        this.publicKey = null;
        this.banned = false;
        this.verified = true;
        this.amountSMSsend = 0;

        Connection conn = MysqlConnector.connectToDatabaseUsingPropertiesFile();
        Statement sta = conn.createStatement();
        ResultSet result = sta.executeQuery("SELECT * FROM users WHERE phonenumber LIKE " + telephoneNumber + " AND ");
    }

    public int getID() {
        return 0;
    }

    public boolean isBanned() {
        return banned;
    }

    public void setBanned(boolean banned) {

    }

    public boolean isVerified(int ID) {
        return verified;
    }

    public void setVerified(int ID, boolean verified) {

    }

    public String getPublicKey() {
        return deviceID;

    }

    public void setPublicKey(String key) {
        //in db
    }

    public int getAmountSMSsend() {
        return amountSMSsend;
    }

    public void setAountSMSsend(int amount) {

    }

    public void raiseAmountSMSsend() {

    }
}

package pixyel_backend.database.objects;

public class User {

    private final int id;
    private final int telephonenumber;
    private final String deviceID;
    private final String publicKey;
    private final boolean banned;
    private final boolean verified;
    private final int amountSMSsend;

    public User(int id) throws Exception {
        this.id = 0;
        this.telephonenumber = 0;
        this.deviceID = null;
        this.publicKey = null;
        this.banned = false;
        this.verified = true;
        this.amountSMSsend = 0;
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

    public void setPublicKey() {

    }

    public int getAmountSMSsend() {
        return amountSMSsend;
    }

    public void setAountSMSsend(int amount) {

    }

    public void raiseAmountSMSsend() {

    }
}

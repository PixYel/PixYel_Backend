/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend.database.objects;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import pixyel_backend.Log;
import pixyel_backend.database.Columns;
import pixyel_backend.database.MysqlConnector;
import pixyel_backend.database.SqlUtils;
import pixyel_backend.database.exceptions.UserCreationException;

/**
 *
 * @author Yannick
 */
public class WebUser {

    /**
     * Validates if the user is maintained in the database and if the password
     * is correct
     *
     * @param username
     * @param pw
     * @return
     */
    public static boolean loginWebUser(String username, String pw) {
        try (PreparedStatement statement = MysqlConnector.getConnection().prepareStatement("SELECT * FROM webusers WHERE " + Columns.NAME + " = ? AND " + Columns.PW + " = ?")) {
            username = SqlUtils.escapeString(username);
            statement.setString(1, username);

            pw = hash(pw);
            pw = SqlUtils.escapeString(pw);
            statement.setString(2, pw);

            ResultSet result = statement.executeQuery();

            if (result == null || !result.isBeforeFirst()) {
                return false;
            } else {
                return true;
            }

        } catch (Exception ex) {
            Log.logError(ex.getMessage(), WebUser.class);
            return false;
        }
    }

    /**
     * Adds a new Webuser to the Database
     * @param username must be between 3 and 25 characters
     * @param pw must be between 4 and 40 characters
     * @throws UserCreationException
     */
    public static void addNewWebUser(String username, String pw) throws UserCreationException {
        if (username == null || username.length() > 25 || username.length() < 3 || pw == null || pw.length() < 4 || pw.length() > 40) {
            throw new UserCreationException();
        }
        try (PreparedStatement statement = MysqlConnector.getConnection().prepareStatement("INSERT INTO webusers(" + Columns.NAME + ", " + Columns.PW + ")VALUES (?,?)")) {
            username = SqlUtils.escapeString(username);
            statement.setString(1, username);

            pw = hash(pw);
            pw = SqlUtils.escapeString(pw);
            statement.setString(2, pw);

            statement.executeUpdate();
        } catch (Exception ex) {
            Log.logWarning("Could not create webuser - rootcause: " + ex, WebUser.class);
            throw new UserCreationException();
        }
    }

    /**
     * 
     * @param username 
     */
    public static void deleteWebUser(String username) {
        try (PreparedStatement sta = MysqlConnector.getConnection().prepareStatement("DELETE FROM webusers WHERE " + Columns.NAME + " = ?")) {
            sta.setString(1, SqlUtils.escapeString(username));
            sta.executeUpdate();

        } catch (Exception e) {
            Log.logWarning("Could not delete webuser \"" + username + "\" - rootcause:" + e, User.class);
        }
    }

    /**
     * 
     * @param toHash
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException 
     */
    private static String hash(String toHash) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] salt = "whatever".getBytes();
        KeySpec spec = new PBEKeySpec(toHash.toCharArray(), salt, 65536, 512);
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        byte[] hash = f.generateSecret(spec).getEncoded();
        Base64.Encoder enc = Base64.getEncoder();
        return enc.encodeToString(hash);
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend.database.objects;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import pixyel_backend.database.DbConnection;

/**
 *
 * @author Da_Groove
 */
public class Picture {

    private int id;
    private String data;
    private double longitude;
    private double latitude;
    private Date timestamp;
    private int upvotes;
    private int downvotes;
    private int flags;
    private List<String> flaggedBy;
    private int userId;
    private String commentIds;

    public Picture(int pictureId, DbConnection con) throws SQLException {
        this.id = pictureId;

        //get Data
        PreparedStatement sta = con.getPreparedStatement("SELECT data FROM picturesData WHERE pictureid LIKE ?");
        sta.setInt(1, this.id);
        ResultSet result = sta.executeQuery();
        result.next();
        this.data = result.getString("data");

        //get Info
        sta = con.getPreparedStatement("SELECT * FROM picturesInfo WHERE pictureid LIKE ?");
        sta.setInt(1, this.id);
        result = sta.executeQuery();
        result.next();
        this.longitude = result.getDouble("longitude");
        this.latitude = result.getDouble("latitude");
        this.timestamp = result.getDate("upload_date");
        this.upvotes = result.getInt("upvotes");
        this.downvotes = result.getInt("downvotes");
        this.userId = result.getInt("userid");

        //get flags
        if (result.getString("flags") != null) {
            String flaggedByAsString = result.getString("flags");
            this.flaggedBy = Arrays.asList(flaggedByAsString);
            this.flags = this.flaggedBy.size();
        } else {
            this.flaggedBy = new ArrayList();
            this.flags = 0;
        }
        
        //get commentIds
        if(result.getString("commentids") != null) {
            String commentedByAsString = result.getString("commentids");
            //this.commentIds = Arrays.asList(commentedByAsString);
        }
    }
}

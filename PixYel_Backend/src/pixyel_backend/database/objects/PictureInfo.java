/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend.database.objects;

import java.util.Date;
import java.util.List;

/**
 *
 * @author Da_Groove
 */
public class PictureInfo {
    private int id;
    private double xCoord;
    private double yCoord;
    private Date timestamp;
    private int upvotes;
    private int downvotes;
    private int flags;
    private int userId;
    private String commentsId;
    
    
    
                    statments.executeUpdate("CREATE TABLE picturesInfo ("
                    + "pictureid INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY, "
                    + "x_Coordinate DOUBLE(30), "
                    + "y_coordinate DOUBLE(30), "
                    + "upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                    + "upvotes INT(6), "
                    + "downvotes INT(6), "
                    + "flags INT(6), "
                    + "userid INT(6), "
                    + "comments MEDIUMTEXT)"
                );
}

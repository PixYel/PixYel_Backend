/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend.database;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import pixyel_backend.database.exceptions.CommentCreationException;
import pixyel_backend.database.exceptions.PictureUploadExcpetion;
import pixyel_backend.database.objects.Comment;
import pixyel_backend.database.objects.Picture;
import pixyel_backend.xml.XML;

/**
 *
 * @author Yannick
 */
public class BackendFunctions {
    public static List<Comment> getCommentsForPicutre(int pictureId) throws SQLException, CommentCreationException {
        return Comment.getCommentsForPicutre(pictureId);
    }

    public static List<Picture> getPicturesList(int xCordinate, int yCordinate) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * 
     * @param listAsString , separated list of all id example: 1,2,4,6
     * @return 
     */
    public static Picture getPicturesData(String listAsString) {
        List<String> allRequestedPictures = Arrays.asList(listAsString);
        for (String currentPictureIdAsString : allRequestedPictures) {
            ////Picutres holen zu XML hinzufügen 
        }
        return null;
    }

    public static void flagPicture(int pictureId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

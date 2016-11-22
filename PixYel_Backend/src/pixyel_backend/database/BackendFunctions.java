/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend.database;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pixyel_backend.database.exceptions.PictureLoadException;
import pixyel_backend.database.objects.Comment;
import pixyel_backend.database.objects.Picture;

/**
 *
 * @author Yannick
 */
public class BackendFunctions {

    public static List<Comment> getCommentsForPicutre(int pictureId) {
        return Comment.getCommentsForPicutre(pictureId);
    }

    

    /**
     * returns a list of max. 100 pictures by using the top ranked pictures
     * inside a given searchradius
     *
     * @param cord
     * @param searchDistance in km
     * @return
     * @throws pixyel_backend.database.exceptions.NoPicturesFoundExcpetion
     */
    

    /**
     * 
     * @param listAsString , separated list of all id example: 1,2,4,6
     * @return A map which contains all requested pictureData with their Id as
     * keys
     */
    public static Map<Integer, String> getPicturesData(String listAsString) {
        HashMap<Integer, String> pictureList = new HashMap<>();
        List<String> allRequestedPictures = Arrays.asList(listAsString);
        for (String currentPictureIdAsString : allRequestedPictures) {
            Integer currentPictureId = Integer.valueOf(currentPictureIdAsString);
            String currentPictureData;
            try {
                currentPictureData = Picture.getDataForId(currentPictureId);
            } catch (PictureLoadException ex) {
                currentPictureData = null;
            }
            pictureList.put(currentPictureId, currentPictureData);
        }
        return pictureList;
    }
}

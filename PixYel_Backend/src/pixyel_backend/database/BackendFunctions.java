/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pixyel_backend.database;

import pixyel_backend.database.exceptions.NoPicturesFoundExcpetion;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import pixyel_backend.Log;
import pixyel_backend.database.dataProcessing.RankingCalculation;
import pixyel_backend.database.exceptions.PictureLoadException;
import pixyel_backend.database.objects.Comment;
import pixyel_backend.database.objects.Coordinate;
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
     * inside a searchradius of 20km (headinformation only)
     *
     * @param cord current location of the user who requests the Photos
     * @return
     * @throws pixyel_backend.database.exceptions.NoPicturesFoundExcpetion
     */
    public static List<Picture> getPicturesList(Coordinate cord) throws NoPicturesFoundExcpetion {
        return getPicturesList(cord, 20);
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
    public static List<Picture> getPicturesList(Coordinate cord, int searchDistance) throws NoPicturesFoundExcpetion {
        List<Picture> pictureList = new LinkedList();
        List<Coordinate> searchArea = cord.getSearchArea(searchDistance);
        try (PreparedStatement sta = MysqlConnector.getConnection().prepareStatement("SELECT id FROM picturesInfo WHERE (longitude BETWEEN ? AND ?) AND (latitude BETWEEN ? AND ?)")) {
            sta.setDouble(1, searchArea.get(0).getLongitude());
            sta.setDouble(2, searchArea.get(1).getLongitude());
            sta.setDouble(3, searchArea.get(0).getLatitude());
            sta.setDouble(4, searchArea.get(1).getLatitude());
            ResultSet result = sta.executeQuery();

            if (result == null || !result.isBeforeFirst()) {
                throw new NoPicturesFoundExcpetion();
            } else {
                while (result.next()) {
                    Picture pic = Picture.getPictureById(result.getInt("id"));
                    pic.setRanking(RankingCalculation.calculateRanking(pic, cord));
                    pictureList.add(pic);
                }
            }
        } catch (SQLException ex) {
            Log.logWarning(ex.toString(), BackendFunctions.class);
            throw new NoPicturesFoundExcpetion();
        } catch (PictureLoadException ex) {
            Log.logWarning(ex.getMessage(), BackendFunctions.class);
        }
        pictureList.sort((Picture pic1, Picture pic2) -> Integer.compare(pic1.getRanking(), pic2.getRanking()));
        if (pictureList.size() > 100) {
            return pictureList.subList(0, 99);
        } else {
            return pictureList;
        }
    }

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

    /**
     *
     * @param listAsString , separated list of all id example: 1,2,4,6
     * @return A map which contains all requested pictures with their Id as
     * keys
     */
    public static Map<Integer, Picture> getPicturesStats(String listAsString) {
        HashMap<Integer, Picture> pictureList = new HashMap<>();
        List<String> allRequestedPictures = Arrays.asList(listAsString);
        for (String currentPictureIdAsString : allRequestedPictures) {
            Integer currentPictureId = Integer.valueOf(currentPictureIdAsString);
            Picture currentPicture;
            try {
                currentPicture = Picture.getPictureById(currentPictureId);
            } catch (PictureLoadException ex) {
                currentPicture = null;
            }
            pictureList.put(currentPictureId, currentPicture);
        }
        return pictureList;
    }
}

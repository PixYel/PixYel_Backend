package pixyel_backend.database.dataProcessing;

import static java.lang.Math.pow;
import java.util.Date;
import pixyel_backend.connection.Utils;
import pixyel_backend.database.objects.Coordinate;
import pixyel_backend.database.objects.Picture;

/**
 *
 * @author Da_Groove
 */
public class RankingCalculation {

    /**
     * Used to calculate the ranking of a picture.
     *
     * @param picture
     * @param userCoordinate curent position of the user as coordinate
     * @return
     */
    public static int calculateRanking(Picture picture, Coordinate userCoordinate) {
        int timeRankingPercentage = 10;
        int distanceRankingPercentage = 20;
        int votesRankingPercentage = 70;
        int ranking;
        ranking = timeRankingPercentage * getTimeRanking(Utils.mergeDates(picture.getUploadDate(), picture.getUploadTime()))
                + distanceRankingPercentage * getDistanceRanking(picture.getCoordinate(), userCoordinate)
                + votesRankingPercentage * getVoteRanking(picture.getUpvotes(), picture.getDownvotes());
        return ranking;
    }
    
    /**
     * Calculates a wordwide ranking which is independ from the current location
     * @param picture
     * @return 
     */
    public static int calculateWorldwideRanking(Picture picture){
        int timeRankingPercentage = 20;
        int votesRankingPercentage = 80;
        int ranking;
        ranking = timeRankingPercentage * getTimeRanking(Utils.mergeDates(picture.getUploadDate(), picture.getUploadTime()))
                + votesRankingPercentage * getVoteRanking(picture.getUpvotes(), picture.getDownvotes());
        return ranking;
    }

    /**
     * Calculates a ranking value based on the age of a picture
     * @param uploadDate
     * @return 
     */
    private static int getTimeRanking(Date uploadDate) {
        Date timestamp = new Date();
        long deltaSeconds = (timestamp.getTime() - uploadDate.getTime()) / 1000;  //Time different in seconds
        int timeRanking = (int) (30-(Math.pow((deltaSeconds / 3600), 1.5) / 3600));      //f(x) = -((dS/3600)^1,1) + 50
        return timeRanking;
    }

    /**
     * Calculates a ranking value based on the distance between picture and user
     * @param coordinate
     * @param userCoordinate
     * @return 
     */
    private static int getDistanceRanking(Coordinate coordinate, Coordinate userCoordinate) {
        int distanceRanking = 0;
        long distance = coordinate.getDistance(userCoordinate);
        if (distance <= 1000) {
            distanceRanking = (int) (-1000 * Math.log(0.001 * distance));       //f(x) = -1000*ln(0,001*distance)
        }
        if (distance < 3017 && distance > 1000) {
            distanceRanking = (int) (pow((-0.0000001 * distance), 2.5));        //f(x) = -0.0000001*distance^2.5+50
        }
        if (distance >= 3017) {
            distanceRanking = (int) (-distance);                                //f(x) = -distance
        }
        return distanceRanking;
    }

    /**
     *  Calculates a ranking value based on the votes which were made for the current picture
     * @param upvotes
     * @param downvotes
     * @return 
     */
    private static int getVoteRanking(int upvotes, int downvotes) {
        int voteRanking;
        voteRanking = (int) (upvotes - Math.pow(downvotes, 1.5));
        return voteRanking;
    }
}

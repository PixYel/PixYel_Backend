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
     * Used to calculate the Ranking of a picture.
     *
     * @param picture
     * @param userCoordinate
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
    
    public static int calculateWorldwideRanking(Picture picture){
        int timeRankingPercentage = 20;
        int votesRankingPercentage = 80;
        int ranking;
        ranking = timeRankingPercentage * getTimeRanking(Utils.mergeDates(picture.getUploadDate(), picture.getUploadTime()))
                + votesRankingPercentage * getVoteRanking(picture.getUpvotes(), picture.getDownvotes());
        return ranking;
    }

    private static int getTimeRanking(Date uploadDate) {
        Date timestamp = new Date();
        long deltaSeconds = (timestamp.getTime() - uploadDate.getTime()) / 1000;  //Time different in seconds
        int timeRanking = (int) (30-(Math.pow((deltaSeconds / 3600), 1.5) / 3600));      //f(x) = -((dS/3600)^1,1) + 50
        return timeRanking;
    }

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

    private static int getVoteRanking(int upvotes, int downvotes) {
        int voteRanking;
        //voteRanking = (int) (50 + upvotes - Math.pow(downvotes, 1.5));
        voteRanking = upvotes-downvotes;
        return voteRanking;
    }
}

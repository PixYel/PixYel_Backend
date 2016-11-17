package pixyel_backend.database.dataProcessing;

import static java.lang.Math.pow;
import java.util.Date;
import pixyel_backend.database.objects.Coordinate;
import pixyel_backend.database.objects.Picture;

/**
 *
 * @author Da_Groove
 */
public class RankingCalculation {
    /**
     * 
     * @param picture
     * @param userCoordinate
     * @return 
     */
    public static int calculateRanking(Picture picture, Coordinate userCoordinate) {
        int timeRankingPercentage = 30;
        int distanceRankingPercentage = 30;
        int votesRankingPercentage = 40;
        int ranking;
        ranking = timeRankingPercentage*getTimeRanking(picture.getTimestamp()) + 
                  distanceRankingPercentage*getDistanceRanking(picture.getCoordinate(), userCoordinate) +
                  votesRankingPercentage*getVoteRanking(picture.getUpvotes(), picture.getDownvotes());
        return ranking;
    }

    private static int getTimeRanking(Date uploadDate) {
        Date timestamp = new Date();
        long deltaSeconds = (timestamp.getTime() - uploadDate.getTime()) / 1000;  //Time different in seconds
        int timeRanking = (int) (-(Math.pow((deltaSeconds / 3600), 1.5) / 3600) + 50);      //f(x) = -((dS/3600)^1,1) + 50
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
        voteRanking = (int)(50+upvotes-Math.pow(downvotes,1.5));
        return voteRanking;
    }
}

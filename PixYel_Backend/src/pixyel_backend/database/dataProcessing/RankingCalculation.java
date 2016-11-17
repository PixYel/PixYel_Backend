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

    public static int calculateRanking(Picture picture, Coordinate userCoordinate) {
        int timeRankingPercentage = 10;
        int distanceRankingPercentage = 10;
        int flagRankingPercentage = 10;
        int upvotesRankingPercentage = 35;
        int downvotesRankingPercentage = 35;
        int ranking = 0;

        return ranking;
    }

    private static int getTimeRanking(Date uploadDate) {
        Date timestamp = new Date();
        long deltaSeconds = (timestamp.getTime() - uploadDate.getTime()) / 1000;  //Time different in seconds
        int timeRanking = (int) (-(Math.pow((deltaSeconds / 3600), 1.5) / 3600) + 50);      //f(x) = -((dS/3600)^1,1) + 50
        return timeRanking;
    }

    private static int getDistanceRanking(Coordinate coordinate, Coordinate userCoordinate) {
        int distanceRanking;
        long distance = coordinate.getDistance(userCoordinate);
        if (distance < 3017) {
            distanceRanking = (int) (pow((-0.0000001 * distance), 2.5) + 50);       //f(x) = -0.0000001*dist^2.5+50
        } else {
            distanceRanking = (int) (-distance);
        }
        return distanceRanking;
    }
}

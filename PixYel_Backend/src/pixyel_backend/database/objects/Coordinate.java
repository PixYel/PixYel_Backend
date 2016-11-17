package pixyel_backend.database.objects;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Da_Groove
 */
public class Coordinate {
    private final double longitude;
    private final double latitude;
    public Coordinate(double longitude,double latitude){
        this.longitude=longitude;
        this.latitude=latitude;
    }

    /**
     * @return the longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * @return the latitude
     */
    public double getLatitude() {
        return latitude;
    }
    
    public Long getDistance(Coordinate toCoordinate) {
        double approximatelatitude = Math.toRadians((this.getLatitude() + toCoordinate.getLatitude()) / 2);
        double differenceXaxis = 111.3 * Math.cos(approximatelatitude) * (this.getLongitude() - toCoordinate.getLongitude());
        double differenceYaxis = 111.3 * (this.getLatitude() - toCoordinate.getLatitude());
        double distance = (Math.sqrt(Math.pow(differenceXaxis, 2) + Math.pow(differenceYaxis, 2)) * 1000);
        return (long) distance;
    }
    
    public List<Coordinate> getSearchArea(int distance){
        Coordinate coordinate1;
        Coordinate coordinate2;
        coordinate1 = new Coordinate(Math.sqrt(Math.pow(this.longitude, 2)-Math.pow(distance, 2)),Math.sqrt(Math.pow(this.longitude, 2)-Math.pow(distance, 2)));
        coordinate2 = new Coordinate(Math.sqrt(Math.pow(this.longitude, 2)-Math.pow((distance*(-1)), 2)),Math.sqrt(Math.pow(this.longitude, 2)-Math.pow((distance*(-1)), 2)));
        List<Coordinate> returnList = new LinkedList();
        returnList.add(coordinate1);
        returnList.add(coordinate2);
        return returnList;
    }
}

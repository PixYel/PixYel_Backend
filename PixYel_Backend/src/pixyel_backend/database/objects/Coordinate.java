package pixyel_backend.database.objects;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Da_Groove
 * 
 */
public class Coordinate {
    private final double longitude;
    private final double latitude;
    /**
     * 
     * @param longitude
     * @param latitude 
     */
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
    
    /**
     * Calculates the distance to a second coordinate
     * @param toCoordinate
     * @return distance in meters between both coordinates
     */
    public Long getDistance(Coordinate toCoordinate) {
        // Algorithm based on https://www.kompf.de/gps/distcalc.html
        double approximatelatitude = Math.toRadians((this.getLongitude() + toCoordinate.getLongitude()) / 2);
        double differenceXaxis = 111.3 * Math.cos(approximatelatitude) * (this.getLatitude() - toCoordinate.getLatitude());
        double differenceYaxis = 111.3 * (this.getLongitude() - toCoordinate.getLongitude());
        double distance = (Math.sqrt(Math.pow(differenceXaxis, 2) + Math.pow(differenceYaxis, 2)) * 1000);
        return (long) distance;
    }
    
    /**
     * Variable SearchArea from a center coordinate.
     * @param distance
     * @return returns two coordinates which represent the diagonal of the border to search for pictures in. This represents a square, of 2dist x 2dist km
     */
    public List<Coordinate> getSearchArea(int distance){
        double approxLat = Math.toRadians(this.longitude);
        double deltaLong = distance/111.3;
        double deltaLat = distance/(111.3*Math.cos(approxLat));
        Coordinate coordinate1 = new Coordinate ((this.longitude-deltaLong),(this.latitude-deltaLat));
        Coordinate coordinate2 = new Coordinate ((this.longitude+deltaLong),(this.latitude+deltaLat));
        List<Coordinate> returnList = new LinkedList();
        returnList.add(coordinate1);
        returnList.add(coordinate2);
        return returnList;
    }
}

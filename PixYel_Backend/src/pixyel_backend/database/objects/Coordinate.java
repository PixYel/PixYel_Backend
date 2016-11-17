package pixyel_backend.database.objects;

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
            //double longitude1, double latitude1, double longitude2, double latitude2
        double approximatelatitude = Math.toRadians((this.getLatitude() + toCoordinate.getLatitude()) / 2);
        double differenceXaxis = 111.3 * Math.cos(approximatelatitude) * (this.getLongitude() - toCoordinate.getLongitude());
        double differenceYaxis = 111.3 * (this.getLatitude() - toCoordinate.getLatitude());
        double distance = (Math.sqrt(Math.pow(differenceXaxis, 2) + Math.pow(differenceYaxis, 2)) * 1000);
        return (long) distance;
    }
}

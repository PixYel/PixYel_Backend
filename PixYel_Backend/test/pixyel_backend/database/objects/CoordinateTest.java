package pixyel_backend.database.objects;

import static org.junit.Assert.assertEquals;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 *
 * @author Da_Groove
 */
public class CoordinateTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * Test if the distancecalculation algorithm works within given specifications
     */
    @Test
    public void testdistanceCalc() {

        Coordinate fromCoordinate = new Coordinate(49.236444, 6.9870489);
        Coordinate toCoordinate = new Coordinate(49.2363241, 6.9859112);

        long solution = fromCoordinate.getDistance(toCoordinate);
        assertEquals((long)(83), solution);

    }
}

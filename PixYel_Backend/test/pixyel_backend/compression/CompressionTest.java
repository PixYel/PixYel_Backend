package pixyel_backend.compression;

import pixyel_backend.connection.compression.Compression;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author i01frajos445
 */
public class CompressionTest {

    /**
     * Test of decompress method, of class Compression.
     */
    @Test
    public void testCompression() {
        String toCompress = "eeeeeeeeeeeeoooooooooooooofjdosvfods$§&%(/)(/%&($&))iohnjt439sdvjnkerio";
        //§ is not UTF8
        assertEquals("", Compression.compress(toCompress));
        //Ohne §
        toCompress = "eeeeeeeeeeeeoooooooooooooofjdosvfods$&%(/)(/%&($&))iohnjt439sdvjnkerio";
        String compressed = Compression.compress(toCompress);
        System.out.println(Compression.decompress(compressed));
        assertEquals("Should have the same output as the input", toCompress, Compression.decompress(compressed));

    }

}

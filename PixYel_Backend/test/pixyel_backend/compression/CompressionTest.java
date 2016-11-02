package pixyel_backend.compression;

import pixyel_backend.connection.compression.Compression;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 *
 * @author i01frajos445
 */
public class CompressionTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * Test of decompress method, of class Compression.
     */
    @Test
    public void testCompression() throws Compression.CompressionException {
        //Ohne §
        String toCompress = "eeeeeeeeeeeeoooooooooooooofjdosvfods$&%(/)(/%&($&))iohnjt439sdvjnkerio";
        String compressed = Compression.compress(toCompress);
        System.out.println(Compression.decompress(compressed));
        assertEquals("Should have the same output as the input", toCompress, Compression.decompress(compressed));
        toCompress = "eeeeeeeeeeeeoooooooooooooofjdosvfods$§&%(/)(/%&($&))iohnjt439sdvjnkerio";
        //§ is not UTF8
        thrown.expect(Compression.CompressionException.class);
        assertEquals("", Compression.compress(toCompress));

    }

}

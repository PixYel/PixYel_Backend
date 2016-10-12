package pixyel_backend.compression;

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
        String toCompress = "eeeeeeeeeeeeoooooooooooooofjdosvfods§$&%(/)(/%&($&))iohnjt439sdvjnkerio";
        String compressed = Compression.compress(toCompress);
        assertEquals("Should have the same output as the input", toCompress, Compression.decompress(compressed));
        
    }
    
}
